package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.common.extensions.toPinYin
import cn.luorenmu.config.entity.CharacterNickNameList
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
    private val characterNames: CharacterNickNameList,
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
        characterList?.let { characters ->
            val character = characters.characters.firstOrNull { character ->
                character.key.lowercase() == characterName.lowercase() || character.name.toPinYin()
                    .lowercase() == characterName.toPinYin().lowercase()
            }?.key ?: findName(characterName)

            character?.let {
                botContainer.getFirstBot().setMsgEmojiLike(messageId, "124")
                return eternalReturnWebPageScreenshot.webCharacterScreenshot(characterName, it, "")
            }
        }
        return null
    }


    /**
     * 查找绰号名
     */
    fun findName(name: String): String? {
        // &符号会被转换为amp; 适用于 黛比&马莲
        val characterPinYin = name.replace(Regex("amp;"), "").toPinYin().lowercase()
        val characterNickNames = characterNames.characterNickNames

        val character = characterNickNames.firstOrNull { character ->
            character.nickName.firstOrNull { nickname ->
                characterPinYin == nickname.toPinYin().lowercase()
            } != null
        }

        return character?.character
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