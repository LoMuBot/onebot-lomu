package cn.luorenmu.repository.entiy

/**
 * @author LoMu
 * Date 2024.07.27 6:13
 */
data class DeepMessage(var reply: String, var needProcess: Boolean, var next: DeepMessage?)