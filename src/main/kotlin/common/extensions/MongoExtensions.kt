package cn.luorenmu.common.extensions

import cn.luorenmu.repository.ActiveSendMessageRepository
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.ActiveMessage
import cn.luorenmu.repository.entiy.KeywordReply
import cn.luorenmu.repository.entiy.OneBotConfig

/**
 * @author LoMu
 * Date 2024.09.09 06:37
 */
fun ActiveSendMessageRepository.checkThenSave(am: ActiveMessage): Boolean {
    synchronized(this) {
        return findByMessageIs(am.message)?.let { false } ?: run {
            save(am)
            true
        }
    }
}

/**
 * @param am is config object
 * @return exist if return false else return true
 */
fun OneBotConfigRepository.checkThenSave(am: OneBotConfig): Boolean {
    synchronized(this) {
        return findFirstByConfigNameAndConfigContent(am.configName, am.configContent)?.let {
            false
        } ?: run {
            save(am)
            true
        }
    }
}

/**
 * @param configName
 * @param groupId
 *
 * true if success else failed
 */
fun OneBotConfigRepository.checkThenDelete(configName: String, groupId: Long): Boolean {
    val groupIdStr = groupId.toString()
    synchronized(this) {
        this.findFirstByConfigNameAndConfigContent(configName, groupIdStr)?.let {
            this.deleteById(it.id!!)
            return true
        }
        return false
    }
}

fun KeywordReplyRepository.checkThenSave(k: KeywordReply): Boolean {
    synchronized(this) {
        return findByKeywordIsAndReplyIs(k.keyword, k.reply)?.let {
            false
        } ?: run {
            save(k)
            true
        }
    }
}
