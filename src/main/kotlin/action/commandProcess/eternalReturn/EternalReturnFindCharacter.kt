package cn.luorenmu.action.commandProcess.eternalReturn

import action.commandProcess.eternalReturn.entity.EternalReturnCharacter
import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.*
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
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
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        var characterName = sender.message.replaceAtToEmpty(sender.botId).trim()
            .replace(Regex(command), "")
            .replaceBlankToEmpty()
            .lowercase()
        val indexMatch = """[0-9]""".toRegex().find(characterName)?.let {
            val index = it.value.toInt()
            characterName = characterName.replace(it.value, "")
            index
        } ?: -1
        return if (characterName.isBlank()) null else {
            eternalReturnFindCharacter(characterName, indexMatch)
        }

    }



    private fun eternalReturnFindCharacter(characterName: String, i: Int): String? {
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

            var weapon = ""
            val weaponStr = StringBuilder()
            weaponStr.append("武器选择:")


            eternalReturnRequestData.characterInfoFind(characterKey, "", "")
                ?.let { characterInfo ->
                    val weaponType =
                        characterInfo.pageProps.dehydratedState.queries.first { it1 -> it1.state.data.weaponType != null }.state.data

                    val sortWeaponTypes =
                        weaponType.weaponTypes!!.sortedBy { weapon -> weapon.key }
                    if (sortWeaponTypes.isEmpty()) {
                        weaponStr.append("不存在")
                    }
                    for ((index, type) in sortWeaponTypes.withIndex()) {
                        weaponStr.append("${index}.${type.name}      ")
                    }
                    weapon = if (i == -1 || i >= sortWeaponTypes.size) {
                        weaponType.weaponType!!.key
                    } else {
                        sortWeaponTypes[i].key
                    }
                } ?: run {
                weaponStr.append("待重构")
            }
            val returnMsg =
                eternalReturnWebPageScreenshot.webCharacterScreenshot(characterKey, weapon, weaponStr.toString())
            return returnMsg
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

}