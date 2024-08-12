package cn.luorenmu.action

import cn.luorenmu.action.commandProcess.eternalReturn.EternalReturnCommandProcess
import cn.luorenmu.entiy.OneBotAllCommands
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.repository.OneBotCommandRespository
import cn.luorenmu.repository.entiy.OneBotCommand
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
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
    private val eternalReturnCommandHandle: EternalReturnCommandProcess,
    private val redisTemplate: RedisTemplate<String, String>,
) {




    private fun isCurrentCommand(command: String, commandName: String, oneBotCommand: OneBotCommand): Boolean {
        return oneBotCommand.commandName == commandName && command.contains(Regex(oneBotCommand.keyword))
    }


    fun process(command: String, senderId: Long, msgId: Int): String {
        val allCommands = redisTemplate.opsForValue()["allCommands"]?.to<OneBotAllCommands>()?.allCommands ?: run{
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



        allCommands.firstOrNull { isCurrentCommand(command, it.commandName, it) }?.let { oneBotCommand ->
            return when (oneBotCommand.commandName) {
                "ff14Bind" -> ff14Bind(senderId)
                "eternalReturnFindPlayers" -> {
                    val nickname = command.replace(Regex(oneBotCommand.keyword), "").trim()
                    if (nickname.isBlank()) "" else eternalReturnCommandHandle.eternalReturnFindPlayers(nickname)
                }

                "eternalReturnLeaderboard" -> {
                    Regex(oneBotCommand.keyword).find(command)?.let { match ->
                        if (match.groupValues.size > 1) {
                            eternalReturnCommandHandle.leaderboard(match.groupValues[1].toInt())
                        } else {
                            ""
                        }
                    } ?: ""
                }

                "eternalReturnReFindPlayers" -> {
                    val nickname = command.replace(Regex(oneBotCommand.keyword), "").trim()
                    if (nickname.isBlank()) "" else {
                        redisTemplate.delete("Eternal_Return_NickName:$nickname")
                        eternalReturnCommandHandle.eternalReturnFindPlayers(nickname)
                    }
                }

                "eternalReturnFindCharacter" -> {
                    var characterName = command.replace(Regex(oneBotCommand.keyword), "").trim()
                    val indexMatch = """[1-9]""".toRegex().find(characterName)?.let {
                        val index = it.value.toInt()
                        characterName = characterName.replace(it.value, "")
                        index
                    } ?: 1
                    if (characterName.isBlank()) "" else {
                        eternalReturnCommandHandle.eternalReturnFindCharacter(characterName, indexMatch)
                    }
                }

                "eternalReturnCutoffs" -> eternalReturnCommandHandle.cutoffs()
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