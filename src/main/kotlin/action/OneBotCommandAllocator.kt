package cn.luorenmu.action

import cn.luorenmu.action.commandProcess.eternalReturn.EternalReturnCommandProcess
import cn.luorenmu.action.commandProcess.onebotCommand.BotCommandProcess
import cn.luorenmu.entiy.OneBotAllCommands
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.repository.OneBotCommandRespository
import cn.luorenmu.repository.entiy.OneBotCommand
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.13 1:52
 */
@Component
class OneBotCommandAllocator(
    private val oneBotCommandRespository: OneBotCommandRespository,
    private val eternalReturnCommandProcess: EternalReturnCommandProcess,
    private val redisTemplate: RedisTemplate<String, String>,
    private val botCommandProcess: BotCommandProcess,
) {


    private fun isCurrentCommand(
        botId: Long,
        command: String,
        commandName: String,
        oneBotCommand: OneBotCommand,
    ): Boolean {
        val atMe = MsgUtils.builder().at(botId).build()
        val command1 = command.replace(atMe, "")
        if (oneBotCommand.needAtMe) {
            if (!command.contains(atMe)) {
                return false
            }
        }
        oneBotCommand.needAdmin?.let {
            if (!it){
                return false
            }
        }
        return oneBotCommand.commandName == commandName && command1.contains(Regex(oneBotCommand.keyword))
    }


    fun process(botId: Long, command: String, groupId: Long, sender: GroupMessageEvent.GroupSender): String {
        val senderId = sender.userId
        val allCommands = redisTemplate.opsForValue()["allCommands"]?.to<OneBotAllCommands>()?.allCommands ?: run {
            synchronized(redisTemplate) {
                // 二次安全检查
                redisTemplate.opsForValue()["allCommands"]?.to<OneBotAllCommands>()?.allCommands ?: run {
                    val allCommands = oneBotCommandRespository.findAll()
                    val oneBotCommands = OneBotAllCommands(allCommands)
                    redisTemplate.opsForValue()["allCommands", oneBotCommands.toJSONString(), 1L] =
                        TimeUnit.DAYS
                    allCommands
                }
            }
        }



        allCommands.firstOrNull { isCurrentCommand(botId, command, it.commandName, it) }?.let { oneBotCommand ->
            val command1 = command.replace(MsgUtils.builder().at(botId).build(), "")
            return when (oneBotCommand.commandName) {
                "ff14Bind" -> ff14Bind(senderId)
                "eternalReturnFindPlayers" -> {
                    val nickname = command1.replace(Regex(oneBotCommand.keyword), "").trim()
                    if (nickname.isBlank()) "" else eternalReturnCommandProcess.eternalReturnFindPlayers(nickname)
                }
                "eternalReturnEmailPush" -> eternalReturnCommandProcess.eternalReturnEmailPush(groupId, sender)
                "eternalReturnLeaderboard" -> {
                    Regex(oneBotCommand.keyword).find(command1)?.let { match ->
                        if (match.groupValues.size > 1) {
                            eternalReturnCommandProcess.leaderboard(match.groupValues[1].toInt())
                        } else {
                            ""
                        }
                    } ?: ""
                }
                "eternalReturnReFindPlayers" -> {
                    val nickname = command1.replace(Regex(oneBotCommand.keyword), "").trim()
                    if (nickname.isBlank()) "" else {
                        redisTemplate.delete("Eternal_Return_NickName:$nickname")
                        eternalReturnCommandProcess.eternalReturnFindPlayers(nickname)
                    }
                }
                "eternalReturnFindCharacter" -> {
                    var characterName = command1.replace(Regex(oneBotCommand.keyword), "").trim()
                    val indexMatch = """[1-9]""".toRegex().find(characterName)?.let {
                        val index = it.value.toInt()
                        characterName = characterName.replace(it.value, "")
                        index
                    } ?: 1
                    if (characterName.isBlank()) "" else {
                        eternalReturnCommandProcess.eternalReturnFindCharacter(characterName, indexMatch)
                    }
                }

                "eternalReturnCutoffs" -> eternalReturnCommandProcess.cutoffs()
                "botCommandBanStudy" -> botCommandProcess.banStudy(groupId, sender.role,senderId)
                "botCommandUnbanStudy" -> botCommandProcess.unbanStudy(groupId, sender.role,senderId)
                "botCommandBanKeyword" -> botCommandProcess.banKeyword(groupId, sender.role,senderId)
                "botCommandUnbanKeyword" -> botCommandProcess.unbanKeyword(groupId, sender.role,senderId)

                else -> ""
            }
        }

        return ""
    }


    // TODO 待完成
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