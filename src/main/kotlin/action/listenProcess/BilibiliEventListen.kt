package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.commandProcess.botCommand.BilibiliEventListenCommand
import cn.luorenmu.action.request.BilibiliRequestData
import cn.luorenmu.common.extensions.sendGroupMsg
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.getVideoPath
import cn.luorenmu.entiy.Request
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.BilibiliVideoRepository
import cn.luorenmu.repository.entiy.BilibiliVideo
import cn.luorenmu.request.RequestController
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import org.springframework.stereotype.Component
import java.io.File
import kotlin.jvm.optionals.getOrNull

/**
 * @author LoMu
 * Date 2024.09.12 21:13
 */
@Component
class BilibiliEventListen(
    private val bilibiliRequestData: BilibiliRequestData,
    private val bilibiliVideoRepository: BilibiliVideoRepository,
    private val bilibiliEventListen: BilibiliEventListenCommand,
) {
    private val bilibiliVideoLongLink = "BV[a-zA-Z0-9]+"
    private val bilibiliVideoShortLink = "((https://bili2233.cn/([a-zA-Z0-9]+))|(https://b23.tv/([a-zA-Z0-9]+)))"


    fun process(bot: Bot, sender: MessageSender) {
        val groupId = sender.groupOrSenderId
        val message = sender.message
        if (!bilibiliEventListen.state(groupId)) {
            if (!message.contains(MsgUtils.builder().at(bot.selfId).build())) {
                return
            }
        }
        val correctMsg = message.replace("\\", "")
        findBilibiliLinkBvid(correctMsg)?.let { bvid ->

            // 视频信息
            val info = bilibiliRequestData.info(bvid) ?: run {
                bot.sendGroupMsg(groupId, "视频信息获取失败")
                return
            }


            val videoPath = getVideoPath("bilibili/$bvid.flv")
            val videoPathCQ = MsgUtils.builder().video(videoPath, "").build()

            bilibiliVideoRepository.findFirstBybvid(bvid)?.let { bilibili ->
                bot.sendGroupMsgLimit(groupId, bilibili.info)
                bilibili.videoPathCQ?.let { videoPathCQ ->

                    // 不为null 但是文件不存在 应当重新下载文件
                    if (File(bilibili.path!!).exists()) {
                        bot.sendGroupMsgLimit(groupId, videoPathCQ)
                        return
                    }

                } ?: run {
                    return
                }

            }

            bilibiliRequestData.bvidToCid(bvid)?.let { videoStreamInfo ->

                val videoInfoStr = """
                    ${MsgUtils.builder().img(info.pic).build()} 
                    作者:< ${info.owner.name} >
                    ${info.title}
                    https://www.bilibili.com/video/$bvid
                """.trimIndent()


                bot.sendGroupMsgLimit(
                    groupId,
                    videoInfoStr
                )

                val limitTime = if (sender.role.roleNumber >= BotRole.ADMIN.roleNumber) 15 else 5

                downloadVideo(bvid, videoStreamInfo.cid, videoPath, limitTime)?.let { success ->
                    if (success) {
                        bilibiliVideoRepository.save(
                            BilibiliVideo(
                                null,
                                bvid,
                                videoPath,
                                videoPathCQ,
                                videoInfoStr
                            )
                        )
                        bot.sendGroupMsgLimit(groupId, videoPathCQ)
                    } else {
                        bilibiliVideoRepository.save(
                            BilibiliVideo(
                                null,
                                bvid,
                                null,
                                null,
                                videoInfoStr
                            )
                        )
                    }
                } ?: run {
                    bot.sendGroupMsg(groupId, "视频下载失败")
                }
            }
        }

    }


    private fun findBilibiliLinkBvid(message: String): String? {
        if (message.contains(bilibiliVideoLongLink.toRegex())) {
            return MatcherData.matcherIndexStr(message, bilibiliVideoLongLink, 0).getOrNull()?.let { bvid ->
                if (bvid.length == 12) {
                    return bvid
                }
                null
            }

        }
        if (message.contains(bilibiliVideoShortLink.toRegex())) {
            // short link to long link
            val shortLink = MatcherData.matcherStr(message, bilibiliVideoShortLink, 1, "").getOrNull()

            val respBody = RequestController(Request.RequestDetailed().apply {
                url = shortLink!!
                method = "GET"
            }).request().body()

            return MatcherData.matcherIndexStr(respBody, bilibiliVideoLongLink, 0).getOrNull()
        }

        return null
    }


    /**
     *  @param bvid (bv号)
     *  @param outputPath video save local path need file name and video type (default type is flv)
     *  @return null if video download failed  else false video too large (limit length $minute minute)
     */
    private fun downloadVideo(bvid: String, cid: Long, outputPath: String, limitTime: Int = 5): Boolean? {
        val videoInfos = bilibiliRequestData.getVideoInfo(bvid, cid)
        videoInfos?.let {
            val minute = videoInfos.timelength / 1000 / 60
            if (limitTime > 5) {
                return false
            }
            videoInfos.durl.firstOrNull()?.let { videoInfo ->
                if (bilibiliRequestData.downloadVideo(videoInfo.url, outputPath)) {
                    return true
                }
                return null
            }
        }
        return false
    }

}