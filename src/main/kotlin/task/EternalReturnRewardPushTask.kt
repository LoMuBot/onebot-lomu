package cn.luorenmu.task

import cn.luorenmu.service.EmailPushService
import cn.luorenmu.action.commandProcess.eternalReturn.entiy.eternalReturn.EternalReturnNews
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.entiy.Request
import cn.luorenmu.repository.EternalReturnPushRepository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.OneBotConfig
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


/**
 * @author LoMu
 * Date 2024.09.01 14:40
 */
@Component
open class EternalReturnRewardPushTask(
    private val oneBotConfigRepository: OneBotConfigRepository,
    private val eternalReturnPushRepository: EternalReturnPushRepository,
    private val emailPushService: EmailPushService,
    val botContainer: BotContainer
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


    @Scheduled(cron = "0 0 * * * ?")
    //@Scheduled(cron = "0 */1 * * * *")
    fun eternalReturnRewardPush() {
        if (failed > 3) {
            log.error { "eternalReturnRewardPush 任务失败超过指定次数当前任务已被拒绝" }
            return
        }
        val requestDetailed = Request.RequestDetailed()
        requestDetailed.url = "https://playeternalreturn.com/api/v1/posts/news?page=1&hl=zh-CN"
        requestDetailed.method = "GET"
        var lastId = 0
        var lastNews: OneBotConfig? = null
        try {
            val body = RequestController(requestDetailed).request().body()
            val eternalReturnNews = body.to<EternalReturnNews>()
            val maxId = eternalReturnNews.articles.maxBy { it.id }.id
            oneBotConfigRepository.findOneByConfigName("lastNews")?.let { oneBotConfig ->
                lastId = oneBotConfig.configContent.toInt()
                findArticleThenPush(lastId, eternalReturnNews)
                lastNews = oneBotConfig
            } ?: run {
                findArticleThenPush(lastId, eternalReturnNews)
            }

            if (lastId != maxId) {
                lastNews?.let {
                    it.configContent = maxId.toString()
                } ?: run {
                    lastNews = OneBotConfig(null, "lastNews", maxId.toString())
                }
                oneBotConfigRepository.save(lastNews!!)
            }

        } catch (e: Exception) {
            log.error { e }
            failed++
        }
        failed = 0


    }

    fun findArticleThenPush(lastId: Int, eternalReturnNews: EternalReturnNews) {
        val groupList = pushGroupList()
        for (article in eternalReturnNews.articles) {
            if (article.id > lastId && article.i18ns.zhCN.title.contains(filterNews.toRegex())) {
                emailPushService.emailPush(
                    eternalReturnPushRepository.findBySendIsTrue().stream().map { it.email }.toList(),
                    "永恒轮回活动推送:${article.i18ns.zhCN.title}",
                    "<img src=\"${article.thumbnailUrl}\" alt=\"img\">"
                )
                asyncPushGroup(groupList, MsgUtils.builder().text("永恒轮回活动推送:${article.i18ns.zhCN.title}").img(article.thumbnailUrl).build())
            }
        }
    }

    @Async
    open fun asyncPushGroup(groupList: List<Long>, msg: String) {
        val bot = botContainer.robots.entries.first().value
        for (group in groupList) {
            TimeUnit.SECONDS.sleep(10)
            bot.sendGroupMsgLimit(group, msg)
        }
    }

    fun pushGroupList(): List<Long> {
        val bot = botContainer.robots.entries.first().value
        val groups =
            oneBotConfigRepository.findAllByConfigName("pushGroup").stream().map<Long> { it.configContent.toLong() }
                .toList()
        return bot.groupList.data.stream().filter {
            it.groupName.contains("永恒轮回") || groups.contains(it.groupId)
        }.map<Long> { it.groupId }.toList()
    }
}