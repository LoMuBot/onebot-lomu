package cn.luorenmu.task

import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnArticle
import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnNews
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.entiy.Request
import cn.luorenmu.repository.EternalReturnPushRepository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entity.OneBotConfig
import cn.luorenmu.request.RequestController
import cn.luorenmu.service.EmailPushService
import com.alibaba.fastjson2.to
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


/**
 * @author LoMu
 * Date 2024.09.01 14:40
 */
@Component
class EternalReturnRewardPushTask(
    private val oneBotConfigRepository: OneBotConfigRepository,
    private val eternalReturnPushRepository: EternalReturnPushRepository,
    private val emailPushService: EmailPushService,
    val botContainer: BotContainer,
) {
    private var failed = 0
    private val log = KotlinLogging.logger { }
    private val filterNews = oneBotConfigRepository.findOneByConfigName("filterNews")?.configContent ?: run {
        oneBotConfigRepository.save(
            OneBotConfig(
                null, "filterNews", "(((?<!冲向永恒.?)活动)|(上线奖励)|(兑换券)|(排位奖励)|(礼物)|(通行证))"
            )
        )
        "(((?<!冲向永恒.?)活动)|(上线奖励)|(兑换券)|(排位奖励)|(礼物)|(通行证))"
    }


    @Scheduled(cron = "0 */5 * * * *")
    //@Scheduled(cron = "0 */1 * * * *")
    fun eternalReturnRewardPush() {
        if (failed > 3) {
            log.error { "eternalReturnRewardPush 任务失败超过指定次数当前任务已被拒绝" }
            return
        }

        var lastId = 0
        var lastNews: OneBotConfig? = null
        try {
            val body = RequestController(Request.RequestDetailed().apply {
                url = "https://playeternalreturn.com/api/v1/posts/news?page=1&hl=zh-CN"
                method = "GET"
            }).request().body()
            val eternalReturnNews = body.to<EternalReturnNews>()

            val format = DateTimeFormatter.ISO_ZONED_DATE_TIME
            val articles = eternalReturnNews.articles.sortedByDescending { LocalDateTime.parse(it.createdAt, format) }
                .filter { it.i18ns.zhCN.title.isNotBlank() }
            val firstId = articles.first().id


            oneBotConfigRepository.findOneByConfigName("lastNews")?.let { oneBotConfig ->
                lastId = oneBotConfig.configContent.toInt()
                findArticleThenPush(lastId, articles)
                lastNews = oneBotConfig
            } ?: run {
                findArticleThenPush(lastId, articles)
            }

            if (lastId != firstId) {
                lastNews?.let {
                    it.configContent = firstId.toString()
                } ?: run {
                    lastNews = OneBotConfig(null, "lastNews", firstId.toString())
                }
                oneBotConfigRepository.save(lastNews!!)
            }

        } catch (e: Exception) {
            log.error { e.printStackTrace() }
            failed++
        }
        failed = 0


    }

    fun findArticleThenPush(lastId: Int, articles: List<EternalReturnArticle>) {
        val groupList = pushGroupList()

        for (article in articles) {
            if (article.id == lastId) {
                return
            }
            if (article.i18ns.zhCN.title.contains(filterNews.toRegex())) {
                emailPushService.emailPush(
                    eternalReturnPushRepository.findBySendIsTrue().stream().map { it.email }.toList(),
                    "永恒轮回活动推送:${article.i18ns.zhCN.title}",
                    "<img src=\"${article.thumbnailUrl}\" alt=\"img\">" +
                            "<a href=\"${article.url}?hl=zh-CN\">访问原链接</a>",

                    )
                asyncPushGroup(
                    groupList,
                    MsgUtils.builder().text("永恒轮回活动推送:${article.i18ns.zhCN.title}").img(article.thumbnailUrl)
                        .text("${article.url}?hl=zh-CN").build()
                )
            }
        }
    }

    @Async
    fun asyncPushGroup(groupList: List<Long>, msg: String) {
        val bot = botContainer.getFirstBot()
        for (group in groupList) {
            TimeUnit.SECONDS.sleep(10)
            bot.sendGroupMsgLimit(group, msg)
        }
    }

    fun pushGroupList(): List<Long> {
        val bot = botContainer.robots.entries.first().value
        val groups =
            oneBotConfigRepository.findAllByConfigName("pushGroup").stream().map { it.configContent.toLong() }
                .toList()
        return bot.groupList.data.stream().filter {
            it.groupName.contains("永恒轮回") || groups.contains(it.groupId)
        }.map { it.groupId }.toList()
    }
}