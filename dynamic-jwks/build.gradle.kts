@file:Suppress("UnstableApiUsage")

plugins {
    id("jvm-test-suite")
}

tasks.bootJar {
    enabled = false
}

tasks.check {
    dependsOn(testing.suites.named("integrationTest"))
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            testType.set(TestSuiteType.INTEGRATION_TEST)
            targets { all { testTask.configure { shouldRunAfter(test) } } }
        }
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

dependencies {
    implementation("com.nimbusds:nimbus-jose-jwt")
    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework.retry:spring-retry")
    compileOnly("org.springframework.vault:spring-vault-core")
    compileOnly("org.springframework.boot:spring-boot-starter-logging")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
    /* Unit Test */
    testImplementation("org.springframework.vault:spring-vault-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    /* Integration Test */
    integrationTestImplementation(project)
    integrationTestImplementation("org.testcontainers:vault")
    integrationTestImplementation("org.testcontainers:junit-jupiter")
    integrationTestImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationTestImplementation("org.springframework.boot:spring-boot-testcontainers")
    integrationTestImplementation("org.springframework.cloud:spring-cloud-starter-vault-config")
}
