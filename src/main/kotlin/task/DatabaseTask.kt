package cn.luorenmu.task

import cn.luorenmu.entiy.WaitDeleteFile
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.OverdueKeywordRepository
import cn.luorenmu.repository.entiy.KeywordReply
import cn.luorenmu.repository.entiy.OverdueKeyword
import com.alibaba.fastjson2.to
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
    private val overdueKeywordRepository: OverdueKeywordRepository,
    private val oneBotConfigRepository: OneBotConfigRepository,
) {

    @Scheduled(cron = "0 */5 * * * *")
    fun timingDeleteFile() {
        val deleteFiles = oneBotConfigRepository.findAllByConfigName("waitDeleteFile")
        for (config in deleteFiles) {
            val deleteFile = config.configContent.to<WaitDeleteFile>()
            if (deleteFile.deleteDate.isBefore(LocalDateTime.now())) {
                File(deleteFile.path).delete()
                oneBotConfigRepository.deleteById(config.id!!)
            }
        }
    }

    @Scheduled(cron = "0 0 12 * * ?")
    fun timingDeleteKeyword() {
        val lists = keywordReplyRepository.findAll()
        val now = LocalDateTime.now()
        val deleteLists = arrayListOf<KeywordReply>()
        for (keyword in lists) {
            keyword.createdDate?.let {
                if (now.plusDays(20)
                        .isBefore(keyword.createdDate) && keyword.triggers != null && keyword.triggers!! > 10 || keyword.triggers!! == 1
                ) {
                    deleteLists.add(keyword)
                }
                if (now.plusDays(7)
                        .isBefore(keyword.createdDate) && keyword.triggers == null || keyword.triggers!! == 0
                ) {
                    deleteLists.add(keyword)
                }

            }
        }
        val overdueKeywordList = deleteLists.stream().map { OverdueKeyword(it) }.toList()
        overdueKeywordRepository.saveAll(overdueKeywordList)
        keywordReplyRepository.deleteAll(deleteLists)
    }

}