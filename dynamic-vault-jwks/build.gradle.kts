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
    implementation("org.slf4j:jul-to-slf4j")
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.vault:spring-vault-core")
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
                implementation(project())
                implementation("org.jspecify:jspecify")
                implementation("org.testcontainers:vault")
                implementation("com.nimbusds:nimbus-jose-jwt")
                implementation(project(":dynamic-jwks"))
                implementation("org.testcontainers:junit-jupiter")
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.springframework.boot:spring-boot-testcontainers")
                implementation("org.springframework.cloud:spring-cloud-starter-vault-config")
            }
        }
    }
}
