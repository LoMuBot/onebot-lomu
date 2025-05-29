package cn.luorenmu.task

import cn.luorenmu.entiy.WaitDeleteFile

import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.KeywordReply
import com.alibaba.fastjson2.to
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.09.03 14:45
 */
@Component
class DatabaseTask(
    private val keywordReplyRepository: KeywordReplyRepository,
) {
    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 12 * * ?")
    fun timingDeleteKeyword() {
        val lists = arrayListOf<KeywordReply>()
        lists.addAll(keywordReplyRepository.findByCreatedDateBeforeAndTriggersGreaterThan(LocalDateTime.now().plusDays(-10),20))
        lists.addAll(
            keywordReplyRepository.findByCreatedDateAfterAndTriggersIsNullOrTriggersIs(
                LocalDateTime.now().plusDays(-7), 0
            )
        )
        log.info { "delete keyword ${lists.size}" }
        keywordReplyRepository.deleteAll(lists)
    }

}