package cn.luorenmu.controller

import cn.luorenmu.repository.ActiveSendMessageRepository
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.OneBotCommandRespository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entity.OneBotConfig
import cn.luorenmu.repository.entiy.ActiveMessage
import cn.luorenmu.repository.entiy.KeywordReply
import cn.luorenmu.repository.entiy.OneBotCommand
import com.github.houbb.opencc4j.util.ZhConverterUtil
import org.springframework.web.bind.annotation.*


/**
 * @author LoMu
 * Date 2024.07.05 9:27
 */

@RestController
@RequestMapping("")
class System(
    private val keywordReplyRepository: KeywordReplyRepository,
    private val oneBotConfigRespository: OneBotConfigRepository,
    private val activeSendMessageRepository: ActiveSendMessageRepository,
    private val oneBotCommandRespository: OneBotCommandRespository,
) {

    @GetMapping("/")
    fun system(): String {
        return "server running success"
    }


    @PostMapping("/command")
    fun saveCommand(@RequestBody body: OneBotCommand): HashMap<String, String> {
        val map = HashMap<String, String>()
        val simple = oneBotCommandRespository.insert(body).toString()
        body.keyword = ZhConverterUtil.toTraditional(body.keyword)
        oneBotCommandRespository.insert(body)
        map["save_data"] = simple
        map["status"] = "ok"
        return map
    }

    @PostMapping("/active_message")
    fun saveActiveMessage(@RequestBody body: ActiveMessage): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["save_data"] = activeSendMessageRepository.save(body).toString()
        map["status"] = "ok"
        return map
    }

    @PostMapping("/save")
    fun saveKeywordReply(@RequestBody body: KeywordReply): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["save_data"] = keywordReplyRepository.save(body).toString()
        map["status"] = "ok"
        return map
    }

    @PostMapping("/config")
    fun saveConfig(@RequestBody body: OneBotConfig): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["save_data"] = oneBotConfigRespository.save(body).toString()
        map["status"] = "ok"
        return map
    }

}