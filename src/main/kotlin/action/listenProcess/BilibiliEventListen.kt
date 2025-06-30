package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.commandProcess.botCommand.BilibiliEventListenCommand
import cn.luorenmu.action.request.BilibiliRequestData
import cn.luorenmu.action.request.RequestData
import cn.luorenmu.action.request.entiy.bilibili.BilibiliVideoInfoData
import cn.luorenmu.action.request.entiy.bilibili.BilibiliVideoInfoStreamData
import cn.luorenmu.common.extensions.sendGroupMsg
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.utils.*
import cn.luorenmu.entiy.Request
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.BilibiliVideoRepository
import cn.luorenmu.repository.entiy.BilibiliVideo
import cn.luorenmu.request.RequestController
import cn.luorenmu.service.entity.BilibiliInfoFreeMarker
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
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
    private val requestData: RequestData,
    private val redisUtils: RedisUtils,
    @Value("\${server.port}")
    private val port: String,
) {
    private val bilibiliVideoLongLink = "BV1[0-9a-zA-Z]{9}"
    private val bilibiliVideoShortLink = "((https://bili2233.cn/([a-zA-Z0-9]+))|(https://b23.tv/([a-zA-Z0-9]+)))"
    private val prefixImagesUrl = "/local_images/bilibili/"
    private val log = KotlinLogging.logger { }
    fun process(bot: Bot, sender: MessageSender) {
        val groupId = sender.groupOrSenderId
        val message = sender.message
        if (!bilibiliEventListen.state(groupId)) {
            return
        }
        val correctMsg = message.replace("\\", "")
        findBilibiliLinkBvid(correctMsg)?.let { bvid ->
            // 视频信息
            val info = bilibiliRequestData.info(bvid) ?: run {
                log.info { "视频信息获取失败 message -> $message" }
                bot.sendGroupMsg(groupId, "视频信息获取失败")
                return
            }

            val videoPath = PathUtils.getVideoPath("bilibili/$bvid.flv")
            val videoPathCQ = MsgUtils.builder().video(videoPath, "").build()
            // 视频限制时长 只有在首次监听到该视频 管理员时长发送才生效 否则不发送
            val limitTime = if (sender.role.roleNumber >= BotRole.ADMIN.roleNumber) 5 else 0
            bilibiliVideoRepository.findFirstBybvid(bvid)?.let { bilibili ->
                bot.sendGroupMsgLimit(groupId, bilibili.info)
                bilibili.videoPathCQ?.let { videoPathCQ ->
                    // 不为null 但是文件不存在 应当重新下载文件
                    if (File(bilibili.path!!).exists()) {
                        bot.sendGroupMsgLimit(groupId, videoPathCQ)
                        return
                    }

                } ?: run {
                    //数据库中没有存储视频地址
                    return
                }

            }


            // 下载视频并发送
            bilibiliRequestData.bvidToCid(bvid)?.let { videoStreamInfo ->
                val videoInfos = bilibiliRequestData.getVideoInfo(bvid, videoStreamInfo.cid)
                videoInfos?.let {
                    val minute = videoInfos.timelength / 1000 / 60
                    val suffixMessage = if (minute > limitTime) "视频过长 不发送" else "视频准备发送中"
                    val videoInfoStr =
                        MsgUtils.builder().reply(sender.messageId).img(getVideoInfoImage(info)).text(suffixMessage)
                            .build()
                    bot.sendGroupMsgLimit(
                        groupId,
                        videoInfoStr
                    )
                    if (minute > limitTime){
                        return
                    }
                    downloadVideo(videoInfos, videoPath)?.let { success ->
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
                        bot.sendGroupMsg(groupId, "视频解析失败")
                    }
                }
            }
        }
    }

    private fun getVideoInfoImage(info: BilibiliVideoInfoData): String {
        val path = PathUtils.getImagePath("bilibili/video_info/${info.bvid}")
        WkhtmltoimageUtils.convertUrlToImage(
            "http://localhost:$port/ftlh/${freeMarkerBuild(info)}", path, mapOf("zoom" to "2")
        )
        return path
    }


    private fun freeMarkerBuild(info: BilibiliVideoInfoData): String {
        val localPic = "${info.bvid}-pic"
        val localAvatarName = UUID.randomUUID()
        requestData.downloadStream(info.pic, PathUtils.getImagePath("bilibili/${localPic}"))
        requestData.downloadStream(info.owner.face, PathUtils.getImagePath("bilibili/${localAvatarName}"))
        val content = FreeMarkerUtils.parseData(
            "bilibili_info.ftlh",
            JSON.parseObject(
                BilibiliInfoFreeMarker(
                    "$prefixImagesUrl$localPic",
                    "$prefixImagesUrl$localAvatarName",
                    info.owner.name,
                    info.title,
                    info.desc
                ).toJSONString()
            )
        )
        redisUtils.setCache("ftlh:${info.bvid}", content, 1, TimeUnit.DAYS)
        return info.bvid
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
    private fun downloadVideo(
        videoInfos: BilibiliVideoInfoStreamData,
        outputPath: String,
    ): Boolean? {
        videoInfos.let {
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