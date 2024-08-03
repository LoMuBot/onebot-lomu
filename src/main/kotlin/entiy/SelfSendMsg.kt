package cn.luorenmu.entiy

/**
 * @author LoMu
 * Date 2024.07.05 8:59
 */
class SelfSendMsg(val messageId: Long, var message: String) {
    constructor(message: String) : this(0L,message) {
        this.message = message;
    }
}