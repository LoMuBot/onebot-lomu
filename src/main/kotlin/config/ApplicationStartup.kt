package cn.luorenmu.config

import cn.luorenmu.service.RandomActiveSendMessage
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.07.30 4:52
 */
@Component
class ApplicationStartup(private val r: RandomActiveSendMessage) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
      r.start()
    }

}