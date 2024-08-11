package cn.luorenmu.action

import cn.luorenmu.action.commandHandle.eternalReturn.EternalReturnCommandProcess
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.repository.OneBotCommandRespository
import cn.luorenmu.repository.entiy.OneBotCommand
import cn.luorenmu.request.RequestController
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.07.13 1:52
 */
@Component
class OneBotCommandAllocator(
    private val oneBotCommandRespository: OneBotCommandRespository,
    private val eternalReturnCommandHandle: EternalReturnCommandProcess,
    private val redisTemplate: RedisTemplate<String, String>,
) {


    fun isCommand(command: String): Boolean {
        val all = oneBotCommandRespository.findAll()
        for (value in all) {
            if (command.contains(Regex(value.keyword))) {
                return true
            }
        }
        return false
    }

    private fun isCurrentCommand(command: String, commandName: String, oneBotCommand: OneBotCommand): Boolean {
        return oneBotCommand.commandName == commandName && command.contains(Regex(oneBotCommand.keyword))
    }

    fun process(command: String, senderId: Long, msgId: Int): String {
        val all = oneBotCommandRespository.findAll()
        for (oneBotCommand in all) {
            if (isCurrentCommand(command, "ff14Bind", oneBotCommand)) {
                return ff14Bind(senderId)
            }

            if (isCurrentCommand(command, "eternalReturnFindPlayers", oneBotCommand)) {
                val nickname = command.replace(Regex(oneBotCommand.keyword), "")
                if (nickname.isBlank()){
                    return ""
                }
                return eternalReturnCommandHandle.eternalReturnFindPlayers(nickname)
            }

            if (isCurrentCommand(command, "eternalReturnLeaderboard", oneBotCommand)) {
                Regex(oneBotCommand.keyword).find(command)?.let {
                    if (it.groupValues.size > 1) {
                        return eternalReturnCommandHandle.leaderboard(it.groupValues[1].toInt())
                    }
                }
            }




            if (isCurrentCommand(command, "eternalReturnReFindPlayers", oneBotCommand)) {
                val nickname = command.replace(Regex(oneBotCommand.keyword), "")
                if (nickname.isBlank()){
                    return ""
                }
                redisTemplate.delete("Eternal_Return_NickName:$nickname")
                return eternalReturnCommandHandle.eternalReturnFindPlayers(nickname)
            }
            if (isCurrentCommand(command, "eternalReturnFindCharacter", oneBotCommand)) {
                var characterName = command.replace(Regex(oneBotCommand.keyword), "")
                val regex = """[1-9]""".toRegex()
                var i = 1
                regex.find(characterName)?.let {
                    i = it.groupValues[0].toInt()
                    characterName = characterName.replace(regex,"")
                }
                if (characterName.isBlank()){
                    return ""
                }
                return eternalReturnCommandHandle.eternalReturnFindCharacter(characterName, i)
            }

            if (isCurrentCommand(command, "eternalReturnCutoffs", oneBotCommand)) {
                return eternalReturnCommandHandle.cutoffs()
            }

        }

        // When the program sends this message, it means that an unknown error has occurred internally
        return ""
    }


    private fun ff14Bind(id: Long): String {
        val requestController = RequestController("ff14_request.create_qrcode")
        val response = requestController.request()
        val path = ReadWriteFile.currentPathFileName("qrcode/${id}.png").substring(1)

        val file = ReadWriteFile.writeStreamFile(
            path,
            response.bodyStream()
        )
        val img = MsgUtils.builder().img(OneBotMedia().file(file.absolutePath).cache(false).proxy(false))
        return img.build()
    }
}