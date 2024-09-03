package cn.luorenmu.task

import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.OverdueKeywordRepository
import cn.luorenmu.repository.entiy.KeywordReply
import cn.luorenmu.repository.entiy.OverdueKeyword
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.09.03 14:45
 */
@Component
class DatabaseTask(
    private val keywordReplyRepository: KeywordReplyRepository,
    private val overdueKeywordRepository: OverdueKeywordRepository,
) {
    @Scheduled(cron = "0 0 12 * * ?")
    fun timingDeleteKeyword() {
        val lists = keywordReplyRepository.findAll()
        val now = LocalDateTime.now()
        val deleteLists = arrayListOf<KeywordReply>()
        for (keyword in lists) {
            keyword.createdDate?.let {
                if (now.plusDays(-20).isBefore(keyword.createdDate)) {
                    deleteLists.add(keyword)
                }
            }
        }
        val overdueKeywordList = deleteLists.stream().map{ OverdueKeyword(it) }.toList()
        overdueKeywordRepository.saveAll(overdueKeywordList)
        keywordReplyRepository.deleteAll(deleteLists)
    }

}