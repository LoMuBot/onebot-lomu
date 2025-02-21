package cn.luorenmu.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


/**
 * @author LoMu
 * Date 2024.09.13 08:55
 */
@Configuration
class ThreadPoolConfig {

    @Bean
    fun keywordProcessThreadPool(): AsyncTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = Runtime.getRuntime().availableProcessors()
            threadNamePrefix = "keyword-process-thread-"
            setAllowCoreThreadTimeOut(true)
        }
    }

    @Bean
    fun asyncProcessThreadPool(): AsyncTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 5
            threadNamePrefix = "async-process-thread-"
            setAllowCoreThreadTimeOut(true)
        }
    }
}