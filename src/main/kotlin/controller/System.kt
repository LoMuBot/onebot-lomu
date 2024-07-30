package cn.luorenmu.controller

import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.repository.ActiveSendMessageRepository
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.OneBotConfigRespository
import cn.luorenmu.repository.entiy.ActiveMessage
import cn.luorenmu.repository.entiy.KeywordReply
import cn.luorenmu.repository.entiy.OneBotConfig
import org.springframework.web.bind.annotation.*


/**
 * @author LoMu
 * Date 2024.07.05 9:27
 */

@RestController
@RequestMapping("")
class System(
    private val keywordReplyRepository: KeywordReplyRepository,
    private val oneBotConfigRespository: OneBotConfigRespository,
    private val activeSendMessageRepository: ActiveSendMessageRepository
) {
    @PostMapping("/")
    fun hello(@RequestBody body: String) {
    }
    @GetMapping("/")
    fun system() : String{
        return "server running success"
    }
    @PostMapping("/active_message")
    fun saveActiveMessage(@RequestBody body: ActiveMessage) :HashMap<String, String>{
        val map = HashMap<String, String>()
        map["save_data"] = activeSendMessageRepository.save(body).toString()
        map["status"] = "ok"
        return map;
    }

    @PostMapping("/save")
    fun saveKeywordReply(@RequestBody body: KeywordReply): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["save_data"] = keywordReplyRepository.save(body).toString()
        map["status"] = "ok"
        return map;
    }

    @PostMapping("/config")
    fun saveConfig(@RequestBody body: OneBotConfig): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["save_data"] = oneBotConfigRespository.save(body).toString()
        map["status"] = "ok"
        return map;
    }

    @PostMapping("/update_json")
    fun updateJson(): HashMap<String, String> {
        ReadWriteFile.readDirJsonToRunStore("keyword")
        val map = HashMap<String, String>()
        map["status"] = "ok"
        return map;
    }
}