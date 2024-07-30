package cn.luorenmu


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableAsync


/**
 * @author LoMu
 * Date 2024.07.04 8:14
 */

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoRepositories
@EnableAsync
class MainApplication

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}

