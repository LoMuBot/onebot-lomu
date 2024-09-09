package cn.luorenmu.action.commandProcess.eternalReturn


import cn.luorenmu.common.extensions.firstPinYin
import cn.luorenmu.service.EmailPushService
import cn.luorenmu.repository.EternalReturnPushRepository
import cn.luorenmu.repository.entiy.EternalReturnPush
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
    private fun correctName(name: String): String {
        return name.replace(Regex("amp;"), "")
    }


    fun eternalReturnFindCharacter(characterTemp: String, i: Int): String {
        val character = correctName(characterTemp)
        val characterFind = eternalReturnRequestData.characterFind()
        var characterKey = ""
        var i1 = i
        if (i <= 0) {
            i1 = 1
        }
        redisTemplate.opsForValue()["Eternal_Return_character:${character}-${i}"]?.let {
            return it
        }
        characterFind?.let {
            val correctCharacter = character.firstPinYin()
            for (character1 in it.characters) {
                if (character1.name.firstPinYin() == correctCharacter) {
                    characterKey = character1.key
                    break
                }
                if (character1.key.lowercase() == character.lowercase()) {
                    characterKey = character1.key
                    break
                }
            }

            if (characterKey.isBlank()) {
                return ""
            }


            var rapier = ""
//            var rapierList: List<String> = listOf()
//            val characterInfo = eternalReturnRequestData.characterInfoFind(characterKey)
//            characterInfo?.let { cI ->
//                if (i1 > cI.pageProps.randomCharacter.masteries.size) {
//                    i1 = 1
//                }
//                rapierList = cI.pageProps.randomCharacter.masteries
//                rapier = cI.pageProps.randomCharacter.masteries[i1 - 1]
//            }
//
//            val rapierStr = StringBuilder()
//            rapierStr.append("武器选择")
//            for (index in rapierList.indices) {
//                rapierStr.append("  ${index + 1}:${rapierList[index]}  ")
//            }

            val rapierStr = StringBuilder()
            rapierStr.append("武器选择:")
//            for (index in rapierList.indices) {
//                rapierStr.append("  ${index + 1}:${rapierList[index]}  ")
//            }
            rapierStr.append("dak.gg对角色的数据请求进行了加密 目前无法获取武器数据")
            return eternalReturnWebPageScreenshot.webCharacterScreenshot(characterKey, rapier, i) + rapierStr.toString()

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