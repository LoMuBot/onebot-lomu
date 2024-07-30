package cn.luorenmu.repository.entiy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author LoMu
 * Date 2024.07.30 3:41
 */
@Document(collection = "one_bot_config")
data class OneBotConfig(
    @Id
    var id: String?,
    @Indexed
    val configName: String,
    val configContent: String
)