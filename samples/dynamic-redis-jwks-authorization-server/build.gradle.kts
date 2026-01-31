plugins {
    id("org.springframework.boot") version "4.0.2"
}

val springCloudVersion = "2025.1.1"

tasks.getByName("dependencyCheckAnalyze") {
    enabled = false
}

tasks.getByName("dependencyCheckAggregate") {
    enabled = false
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation("org.jspecify:jspecify")
    implementation(project(":dynamic-redis-jwks-spring-boot-starter"))
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.session:spring-session-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    /* Tool */
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    /* Test */
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
