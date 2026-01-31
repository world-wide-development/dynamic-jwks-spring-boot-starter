plugins {
    id("org.springframework.boot") version "4.0.2"
}

val authServerVersion = "1.5.5"
val springCloudVersion = "2025.1.1"

tasks.getByName("dependencyCheckAnalyze") {
    enabled = false
}

tasks.getByName("dependencyCheckAggregate") {
    enabled = false
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
