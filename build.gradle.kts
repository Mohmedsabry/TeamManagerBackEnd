val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0-rc-2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-webjars-jvm")
    implementation("org.webjars:jquery:3.2.1")
    implementation("io.github.smiley4:ktor-swagger-ui:2.9.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    // ktorm DB
    implementation("org.ktorm:ktorm-core:4.0.0")
    implementation("com.mysql:mysql-connector-j:9.0.0")
    implementation("org.mindrot:jbcrypt:0.4")
    // webServer
    implementation("io.ktor:ktor-websockets:2.3.12")
    implementation("io.ktor:ktor-server-websockets:2.3.12")
    implementation("io.ktor:ktor-server-sessions:2.3.12")
    // mongo
    val kmongo_version = "4.5.0"
    implementation("org.litote.kmongo:kmongo:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")
    implementation("org.mongodb:mongodb-driver-reactivestreams:1.15.0") // Add this if not already included
    implementation("io.projectreactor:reactor-core:3.4.22") // Add reactor-core
    // koin
    val koin_version = "4.0.0"
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    // jwt
    implementation("io.ktor:ktor-server-auth:3.0.0-rc-2")
    implementation("io.ktor:ktor-server-auth-jwt:3.0.0-rc-2")
    // mail
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation(kotlin("script-runtime"))
}
