plugins {
    id("org.springframework.boot") version "3.5.4"
}

val springCloudVersion = "2025.0.0"

tasks.getByName("dependencyCheckAnalyze") {
    enabled = false
}

tasks.getByName("dependencyCheckAggregate") {
    enabled = false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.jspecify:jspecify")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }
}
