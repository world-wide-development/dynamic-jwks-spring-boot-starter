@file:Suppress("unused", "UnstableApiUsage")

plugins {
    id("java")
    id("jvm-test-suite")
    id("jacoco-report-aggregation")
    id("org.jreleaser") version "1.19.0"
    id("org.owasp.dependencycheck") version "12.1.3"
    id("io.spring.dependency-management") version "1.1.7"
}

extra["slf4jVersion"] = "2.0.17"
extra["jSpecifyVersion"] = "1.0.0"
extra["jacksonVersion"] = "2.19.2"
extra["nimbusJoseVersion"] = "10.4"
extra["springBootVersion"] = "3.5.4"
extra["springVaultVersion"] = "3.2.0"
extra["bouncyCastleVersion"] = "1.81"
extra["springRetryVersion"] = "2.0.12"
extra["equalsVerifierVersion"] = "4.0.6"
extra["testcontainersVersion"] = "1.21.3"
extra["springFrameworkVersion"] = "6.2.9"
extra["commonsCompressVersion"] = "1.27.1"
extra["springIntegrationVersion"] = "6.4.3"
extra["springVaultStarterVersion"] = "4.3.0"

extra["nvdApiKey"] = findProperty("nvd.api.key") ?: System.getenv("NVD_API_KEY")

tasks.jar {
    enabled = false
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
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
        }
    }
}

allprojects {

    apply(plugin = "java")
    apply(plugin = "jvm-test-suite")
    apply(plugin = "jacoco-report-aggregation")
    apply(plugin = "org.owasp.dependencycheck")
    apply(plugin = "io.spring.dependency-management")

    version = "0.1.7"
    group = "io.github.world-wide-development"

    repositories {
        mavenCentral()
    }

    dependencyCheck {
        analyzers.apply {
            assemblyEnabled = false
            nodeAudit.enabled = false
            nodePackage.enabled = false
        }
        nvd.apply {
            apiKey = "${property("nvdApiKey")}"
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencyManagement {
        dependencies {
            dependency("org.springframework:spring-web:${property("springFrameworkVersion")}")
            dependency("org.springframework:spring-context:${property("springFrameworkVersion")}")
            dependency("org.apache.commons:commons-compress:${property("commonsCompressVersion")}")

            dependency("org.slf4j:jul-to-slf4j:${property("slf4jVersion")}")
            dependency("org.jspecify:jspecify:${property("jSpecifyVersion")}")
            dependency("com.nimbusds:nimbus-jose-jwt:${property("nimbusJoseVersion")}")
            dependency("org.testcontainers:vault:${property("testcontainersVersion")}")
            dependency("org.bouncycastle:bcpkix-jdk18on:${property("bouncyCastleVersion")}")
            dependency("org.testcontainers:junit-jupiter:${property("testcontainersVersion")}")
            dependency("com.fasterxml.jackson.core:jackson-core:${property("jacksonVersion")}")
            dependency("org.springframework.retry:spring-retry:${property("springRetryVersion")}")
            dependency("com.fasterxml.jackson.core:jackson-databind:${property("jacksonVersion")}")
            dependency("nl.jqno.equalsverifier:equalsverifier:${property("equalsVerifierVersion")}")
            dependency("com.fasterxml.jackson.core:jackson-annotations:${property("jacksonVersion")}")
            dependency("org.springframework.vault:spring-vault-core:${property("springVaultVersion")}")
            dependency("org.springframework.boot:spring-boot-starter-test:${property("springBootVersion")}")
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
