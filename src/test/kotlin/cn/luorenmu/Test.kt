package cn.luorenmu

import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.repository.OneBotCommandConfigRepository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entity.OneBotCommandConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.12.13 00:14
 */


class Test(
    @Autowired val oneBotConfigRepository: OneBotConfigRepository,
    @Autowired val oneBotCommandConfigRepository: OneBotCommandConfigRepository,
) {


    fun configConvert() {
        val all = oneBotConfigRepository.findAll()
        val list: MutableList<OneBotCommandConfig> = mutableListOf()
        for (oneBotConfig in all) {
            val configName = oneBotConfig.configName
            val configContent = oneBotConfig.configContent
            when (configName) {
                "BilibiliEventListen" -> {
                    list.add(
                        initCommandConfig(true, "BilibiliEventListenCommand", configContent)
                    )
                }

                "banKeywordGroup" -> {
                    list.add(
                        initCommandConfig(false, "KeywordSend", configContent)
                    )
                }

                "banStudy" -> {
                    list.add(
                        initCommandConfig(false, "ChatStudy", configContent)
                    )
                }
            }

        }
        oneBotCommandConfigRepository.saveAll(list)
    }

    fun initCommandConfig(state: Boolean, commandConfigName: String, configContent: String): OneBotCommandConfig {
        return OneBotCommandConfig(
            null,
            commandConfigName,
            state,
            BotRole.GroupAdmin,
            configContent.toLong(),
            configContent.toLong(),
            LocalDateTime.now()
        )
    }
}
