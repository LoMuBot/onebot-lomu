package cn.luorenmu.action.commandProcess.eternalReturn

import action.commandProcess.eternalReturn.entity.EternalReturnCharacter
import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.common.extensions.toPinYin
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.core.BotContainer
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:30
 */
@Component("eternalReturnFindCharacter")
class EternalReturnFindCharacter(
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val eternalReturnWebPageScreenshot: EternalReturnWebPageScreenshot,
    private val botContainer: BotContainer,
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        var characterName = sender.message.replaceAtToEmpty(sender.botId).trim()
            .replace(command(), "")
            .replaceBlankToEmpty()
            .lowercase()
        val indexMatch = """[0-9]""".toRegex().find(characterName)?.let {
            val index = it.value.toInt()
            characterName = characterName.replace(it.value, "")
            index
        } ?: -1
        return if (characterName.isBlank()) null else {
            eternalReturnFindCharacter(characterName, indexMatch, sender.messageId.toString())
        }

    }


    private fun eternalReturnFindCharacter(characterName: String, i: Int, messageId: String): String? {
        val characterList = eternalReturnRequestData.characterFind()
        var characterKey = ""

        characterList?.let {
            val character = correctName(characterName, characterList)
            val characterPinYin = character.toPinYin()
            for (character1 in it.characters) {
                // 转换为首拼
                if (character1.name.toPinYin().lowercase() == characterPinYin.lowercase()) {
                    characterKey = character1.key
                    break
                }

                // 英文角色名
                if (character1.key.lowercase() == character.lowercase()) {
                    characterKey = character1.key
                    break
                }
            }

            if (characterKey.isBlank()) {
                return null
            }

            botContainer.getFirstBot().setMsgEmojiLike(messageId, "124")
            return eternalReturnWebPageScreenshot.webCharacterScreenshot(characterKey, "")
        }
        return null
    }


    /**
     * 实验体绰号名修正
     * 字符修正
     */
    private fun correctName(name: String, characterList: EternalReturnCharacter): String {
        // 目前没有考虑支持 优先使用双子
        if (name == "双子" || name.toPinYin() == "黛比马莲".toPinYin()) {
            return characterList.characters.first { it.key == "DebiMarlene" }.name
        }
        return name.replace(Regex("amp;"), "")
    }

    override fun commandName(): String {
        return "eternalReturnFindCharacter"
    }

    override fun state(id: Long): Boolean {
        return true
    }

    override fun command(): Regex = Regex("^((查询角色)|(角色查询)|(查询英雄)|(查詢角色)|(查询实验体))")

    override fun needAtBot(): Boolean = false

}