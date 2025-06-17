package cn.luorenmu.action.commandProcess.botCommand

import cn.hutool.core.codec.Base64Decoder
import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.config.entity.CharacterNickName
import cn.luorenmu.config.entity.CharacterNickNameList
import cn.luorenmu.entiy.Request
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.JSON
import org.springframework.stereotype.Component
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author LoMu
 * Date 2025.06.11 12:17
 */
@Component
class CharacterNameUpdateCommand(
    private val characterNickName: CharacterNickNameList,
) : CommandProcess {
    companion object {

        fun getCharacterNickName(): CharacterNickNameList {
            val requestController = RequestController(Request.RequestDetailed().apply {
                url = "https://api.github.com/repos/LoMuBot/EternalReturn-Alias/contents/character.txt?ref=main"
                method = "get"
            })
            val resp = requestController.request()
            val json = JSON.parseObject(resp.body())

            val decode = Base64Decoder.decode(json["content"].toString())
            val str = String(decode)
            val lines = str.lines()
            val characterNickNames = CopyOnWriteArrayList<CharacterNickName>()
            lines.forEach {
                if (it.isNotBlank()) {
                    val list = it.split(":").toMutableList()
                    list.removeIf { l -> l.isBlank() }
                    val first = list.removeFirst()
                    characterNickNames.add(CharacterNickName(first, list))
                }
            }
            return CharacterNickNameList(characterNickNames)
        }
    }

    override fun process(sender: MessageSender): String? {
        if (sender.role.roleNumber >= BotRole.ADMIN.roleNumber) {
            val newCharacterNickName = getCharacterNickName()
            characterNickName.characterNickNames.clear()
            characterNickName.characterNickNames.addAll(newCharacterNickName.characterNickNames)
            return "已完成"
        }
        return null
    }

    override fun commandName(): String = "CharacterNameUpdate"

    override fun state(id: Long): Boolean = true

    override fun command(): Regex = Regex("^更新角色名称$")

    override fun needAtBot(): Boolean = true
}