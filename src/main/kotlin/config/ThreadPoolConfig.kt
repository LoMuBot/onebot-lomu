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
open class ThreadPoolConfig {

    @Bean
    open fun keywordProcessThreadPool(): AsyncTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 5
            threadNamePrefix = "keyword-process-thread-"
            setAllowCoreThreadTimeOut(true)
        }
    }

    @Bean
    open fun asyncProcessThreadPool(): AsyncTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = Runtime.getRuntime().availableProcessors() / 2
            threadNamePrefix = "async-process-thread-"
            setAllowCoreThreadTimeOut(true)
        }
    }
}