@file:Suppress("UnstableApiUsage")

plugins {
    id("java")
    id("jvm-test-suite")
    id("jacoco-report-aggregation")
    id("org.owasp.dependencycheck") version "9.0.8"
    id("io.spring.dependency-management") version "1.1.4"
}

extra["slf4jVersion"] = "2.0.9"
extra["snakeYamlVersion"] = "2.2"
extra["jacksonVersion"] = "2.16.1"
extra["nimbusJoseVersion"] = "9.36"
extra["springBootVersion"] = "3.1.7"
extra["springRetryVersion"] = "2.0.5"
extra["springVaultVersion"] = "3.1.1"
extra["bouncyCastleVersion"] = "1.77"
extra["equalsVerifierVersion"] = "3.15.4"
extra["testcontainersVersion"] = "1.18.3"
extra["springFrameworkVersion"] = "6.1.2"
extra["springIntegrationVersion"] = "6.2.1"
extra["springVaultStarterVersion"] = "4.1.0"
extra["nvdApiKey"] = findProperty("nvd.api.key") ?: System.getenv("NVD_API_KEY")

tasks.jar {
    enabled = false
}

dependencyCheck {
    analyzers.apply {
        nodeEnabled = false
        nodeAudit.apply {
            enabled = false
        }
    }
    nvd.apply {
        apiKey = "${property("nvdApiKey")}"
    }
}

dependencies {
    jacocoAggregation(project(":dynamic-jwks"))
    jacocoAggregation(project(":dynamic-vault-jwks"))
    jacocoAggregation(project(":dynamic-redis-jwks"))
    jacocoAggregation(project(":dynamic-vault-jwks-spring-boot"))
    jacocoAggregation(project(":dynamic-redis-jwks-spring-boot"))
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            testType.set(TestSuiteType.UNIT_TEST)
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            testType.set(TestSuiteType.INTEGRATION_TEST)
        }
    }
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "jvm-test-suite")
    apply(plugin = "jacoco-report-aggregation")
    apply(plugin = "org.owasp.dependencycheck")
    apply(plugin = "io.spring.dependency-management")

    version = "0.0.9"
    group = "io.github.world-wide-development"

}

allprojects {

    repositories {
        mavenCentral()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    dependencyManagement {
        dependencies {
            dependency("org.yaml:snakeyaml:${property("snakeYamlVersion")}")
            dependency("org.slf4j:jul-to-slf4j:${property("slf4jVersion")}")
            dependency("com.nimbusds:nimbus-jose-jwt:${property("nimbusJoseVersion")}")
            dependency("org.testcontainers:vault:${property("testcontainersVersion")}")
            dependency("org.bouncycastle:bcpkix-jdk18on:${property("bouncyCastleVersion")}")
            dependency("org.springframework:spring-core:${property("springFrameworkVersion")}")
            dependency("org.testcontainers:junit-jupiter:${property("testcontainersVersion")}")
            dependency("org.springframework.retry:spring-retry:${property("springRetryVersion")}")
            dependency("com.fasterxml.jackson.core:jackson-databind:${property("jacksonVersion")}")
            dependency("nl.jqno.equalsverifier:equalsverifier:${property("equalsVerifierVersion")}")
            dependency("org.springframework.data:spring-data-redis:${property("springBootVersion")}")
            dependency("com.fasterxml.jackson.core:jackson-annotations:${property("jacksonVersion")}")
            dependency("org.springframework.vault:spring-vault-core:${property("springVaultVersion")}")
            dependency("org.springframework.boot:spring-boot-starter-test:${property("springBootVersion")}")
            dependency("org.springframework.boot:spring-boot-starter-json:${property("springBootVersion")}")
            dependency("org.springframework.boot:spring-boot-autoconfigure:${property("springBootVersion")}")
            dependency("org.springframework.boot:spring-boot-testcontainers:${property("springBootVersion")}")
            dependency("org.springframework.boot:spring-boot-starter-data-redis:${property("springBootVersion")}")
            dependency("org.springframework.boot:spring-boot-starter-validation:${property("springBootVersion")}")
            dependency("org.springframework.boot:spring-boot-autoconfigure-processor:${property("springBootVersion")}")
            dependency("org.springframework.boot:spring-boot-configuration-processor:${property("springBootVersion")}")
            dependency("org.springframework.integration:spring-integration-core:${property("springIntegrationVersion")}")
            dependency("org.springframework.integration:spring-integration-redis:${property("springIntegrationVersion")}")
            dependency("org.springframework.cloud:spring-cloud-starter-vault-config:${property("springVaultStarterVersion")}")
        }

    }

}
