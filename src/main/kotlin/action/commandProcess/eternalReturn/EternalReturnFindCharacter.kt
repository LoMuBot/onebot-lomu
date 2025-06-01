package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.common.extensions.toPinYin
import cn.luorenmu.config.entity.CharacterNickName
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

        val originName = characterName
        val indexMatch = """[0-9]""".toRegex().find(characterName)?.let {
            val index = it.value.toInt()
            characterName = characterName.replace(it.value, "")
            index
        } ?: -1
        return if (characterName.isBlank()) null else {
            // 对包含数字的名称进行特殊处理(暂时)
            if (originName.lowercase() == "c0" || originName.lowercase() == "u4dn" || originName.lowercase() == "11") eternalReturnFindCharacter(
                originName,
                -1,
                sender.messageId.toString()
            ) else
                eternalReturnFindCharacter(characterName, indexMatch, sender.messageId.toString())
        }

    }


    private fun eternalReturnFindCharacter(characterName: String, i: Int, messageId: String): String? {
        val characterList = eternalReturnRequestData.characterFind()
        characterList?.let { characters ->
            val findName = findName(characterName)
            val character = characters.characters.firstOrNull { character ->
                character.key.lowercase() == characterName.lowercase() || character.name.toPinYin()
                    .lowercase() == characterName.toPinYin().lowercase()
            }?.key ?: findName?.character

            val inputName =
                findName?.nickName?.firstOrNull { it.toPinYin().lowercase() == characterName.toPinYin().lowercase() }
                    ?: characterName

            character?.let {
                val weaponTypes = characterList.characters.first { c -> c.key == it }.weaponTypes
                var weaponType = ""
                if (i != -1 && i < weaponTypes.size) {
                    weaponType = weaponTypes[i].key
                }
                botContainer.getFirstBot().setMsgEmojiLike(messageId, "124")
                return eternalReturnWebPageScreenshot.webCharacterScreenshot(inputName, it, weaponType) +
                        if (weaponTypes.size > 1) "角色武器:${
                            weaponTypes.withIndex()
                                .joinToString(", ") { weapon -> "${weapon.index}. ${findWeaponName(weapon.value.key)}" }
                        }" else ""
            }
        }
        return null
    }


    fun findWeaponName(name: String): String? {
        return eternalReturnRequestData.getWeapons()?.let {
            return it.masteries.first { weapon -> weapon.key == name }.name
        }
    }

    /**
     * 查找绰号名
     */
    fun findName(name: String): CharacterNickName? {
        // &符号会被转换为amp; 适用于 黛比&马莲
        val characterPinYin = name.replace(Regex("amp;"), "").toPinYin().lowercase()
        val characterNickNames = characterNames.characterNickNames

        val character = characterNickNames.firstOrNull { character ->
            character.nickName.firstOrNull { nickname ->
                characterPinYin == nickname.toPinYin().lowercase()
            } != null
        }

        return character
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