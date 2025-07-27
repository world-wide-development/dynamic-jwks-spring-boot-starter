@file:Suppress("unused", "UnstableApiUsage")

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.encoding("UTF-8")
}

dependencies {
    implementation("org.jspecify:jspecify")
    implementation(project(":dynamic-jwks"))
    implementation(project(":dynamic-redis-jwks"))
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.springframework.data:spring-data-redis")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.integration:spring-integration-core")
    implementation("org.springframework.integration:spring-integration-redis")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
    }
}
