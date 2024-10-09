package cn.luorenmu.action.commandProcess.eternalReturn


import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnCharacter
import cn.luorenmu.common.extensions.toPinYin
import cn.luorenmu.repository.EternalReturnPushRepository
import cn.luorenmu.repository.entiy.EternalReturnPush
import cn.luorenmu.service.EmailPushService
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.31 0:09
 */
@Component
class EternalReturnCommandProcess(
    private val redisTemplate: RedisTemplate<String, String>,
    private val eternalReturnDraw: EternalReturnDraw,
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val eternalReturnWebPageScreenshot: EternalReturnWebPageScreenshot,
    private val emailPushService: EmailPushService,
    private val eternalReturnPushRepository: EternalReturnPushRepository,
) {
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


    fun eternalReturnFindCharacter(characterName: String, i: Int): String {
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
                return ""
            }

            var weapon = ""
            val weaponStr = StringBuilder()
            weaponStr.append("武器选择:")
            eternalReturnRequestData.characterInfoFind(characterKey,"")?.let {
                val weaponType = it.pageProps.dehydratedState.queries.filter { it.state.data.weaponType != null }
                    .first().state.data

                val sortWeaponTypes =
                    weaponType.weaponTypes!!.sortedBy { it.key }
                if (sortWeaponTypes.isEmpty()) {
                    weaponStr.append("不存在")
                }
                for ((index,type) in sortWeaponTypes.withIndex()) {
                    weaponStr.append("${index}.${type.name}      ")
                }
                weapon = if (i == -1 || i >= sortWeaponTypes.size) {
                    weaponType.weaponType!!.key
                } else {
                    sortWeaponTypes[i].key
                }


            }


            val returnMsg = eternalReturnWebPageScreenshot.webCharacterScreenshot(characterKey, weapon,weaponStr.toString())

            return returnMsg
        }

        return ""
    }

    fun eternalReturnEmailPush(groupId: Long, sender: GroupMessageEvent.GroupSender): String {
        val email = "${sender.userId}@qq.com"

        eternalReturnPushRepository.findByEmail(email)?.let {
            return "推送列表中已收录了你的邮箱地址"
        }

        emailPushService.emailPush(
            email, "Test Email", "${sender.userId} " +
                    "<img src=\"https://i0.hdslb.com/bfs/new_dyn/eafcdc2ea38eddfbb8a0a1f06fe13e6214868240.png\" alt=\"img\">"
        )
        eternalReturnPushRepository.save(EternalReturnPush(null, email, sender, LocalDateTime.now(), groupId, true))

        return "已向你的qq邮件发送了一封测试邮件"
    }

    fun eternalReturnFindPlayers(nickname: String): String {
        // check name rule
        if (nickname.isBlank() || nickname.contains("@") || nickname.length < 2) {
            return MsgUtils.builder().text("名称不合法 -> $nickname").build()
        }

        val opsForValue = redisTemplate.opsForValue()

        // check cache
        val nicknameData = opsForValue["Eternal_Return_NickName:$nickname"]
        if (nicknameData != null) {
            return nicknameData
        }

        // check name exist and sync data
        if (!eternalReturnRequestData.findExistPlayers(nickname)) {
            val notFound = MsgUtils.builder().text("不存在的玩家 -> $nickname").build()
            opsForValue["Eternal_Return_NickName:$nickname", notFound, 7L] = TimeUnit.DAYS
            return notFound
        }



        return eternalReturnWebPageScreenshot.webPlayerPageScreenshot(nickname)
    }


    // 排名
    fun leaderboard(i: Int): String {
        return eternalReturnDraw.leaderboard(i)
    }


    fun characterLeaderborad(characterChineseName: String, sortType: String) {
        eternalReturnRequestData.characterFind()?.let {
            var characterKey = "Jackie"
            for (character in it.characters) {
                if (character.name == characterChineseName) {
                    characterKey = character.key
                }
            }
            eternalReturnRequestData.characterLeaderboardFind(characterKey, sortType)?.characterById

        }
    }

    //分数限
    fun cutoffs(): String {
        return eternalReturnDraw.cutoffs()
    }


}