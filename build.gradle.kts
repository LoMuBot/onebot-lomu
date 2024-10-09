plugins {
    kotlin("jvm") version "2.0.0"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.spring") version "2.0.0"
}



group = "cn.luorenmu"
version = "1.0-SNAPSHOT"

repositories {
    maven(url = "https://maven.aliyun.com/repository/public/")
    maven(url = "https://maven.aliyun.com/repository/spring/")

    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}
dependencies {


    implementation("cn.hutool:hutool-all:5.8.29")
    implementation("com.google.zxing:core:3.5.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.mikuac:shiro:2.3.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.52")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation(files("lib/MultifunctionalAutoHelper-Java.jar"))
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // 分词
    implementation("org.ansj:ansj_seg:5.1.6")
    // pinyin
    implementation("com.github.promeg:tinypinyin:2.0.3")

    // Kotlin test dependencies
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "cn.luorenmu.MainApplication"
    }
}