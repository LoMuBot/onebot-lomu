package cn.luorenmu.exception

/**
 * @author LoMu
 * Date 2025.05.30 15:18
 * Description: 该异常消息将作为回复抛给用户
 */
class LoMuBotException(val msg: String) : RuntimeException() {
}