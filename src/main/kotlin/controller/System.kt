package cn.luorenmu.controller

import cn.luorenmu.action.render.EternalReturnFindPlayerRender
import cn.luorenmu.repository.ActiveSendMessageRepository
import cn.luorenmu.repository.OneBotCommandConfigRepository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entity.OneBotCommandConfig
import cn.luorenmu.repository.entity.OneBotConfig
import cn.luorenmu.repository.entiy.ActiveMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


/**
 * @author LoMu
 * Date 2024.07.05 9:27
 */

@RestController
@RequestMapping("")
class System(
    private val eternalReturnRender: EternalReturnFindPlayerRender,
    private val oneBotConfigRespository: OneBotConfigRepository,
    private val activeSendMessageRepository: ActiveSendMessageRepository,
    private val oneBotCommandConfigRepository: OneBotCommandConfigRepository,

    ) {
    @PostMapping("/command_config")
    fun commandConfig(@RequestBody oneBotCommandConfig: OneBotCommandConfig): HashMap<String, String> {
        val map = HashMap<String, String>()
        val save = oneBotCommandConfigRepository.save(oneBotCommandConfig)
        map["save_data"] = save.toString()
        map["status"] = "ok"
        return map
    }

    @GetMapping("/")
    fun system(): String {
        return "server running success"
    }

    @GetMapping("/search/{name}")
    fun search(@PathVariable name: String): String {
        return eternalReturnRender.imageRenderGenerate(name)
    }

    @PostMapping("/active_message")
    fun saveActiveMessage(@RequestBody body: ActiveMessage): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["save_data"] = activeSendMessageRepository.save(body).toString()
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