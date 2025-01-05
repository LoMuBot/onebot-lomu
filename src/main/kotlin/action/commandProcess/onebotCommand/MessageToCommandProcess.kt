package cn.luorenmu.action.commandProcess.onebotCommand

import cn.luorenmu.action.PermissionsManager
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.MessageToCommandRepository

/**
 * @author LoMu
 * Date 2024.12.13 13:00
 */
class MessageToCommandProcess(
    private val permissionsManager: PermissionsManager,
    private val messageToCommandRepository: MessageToCommandRepository,
) {
    fun addKeyToMessage(command: String, messageSender: MessageSender): String? {
        if (permissionsManager.isAdmin(messageSender.role, messageSender.senderId)) {
            val split = command.split(" ")

            return "保存成功"
        }
        return null
    }
}