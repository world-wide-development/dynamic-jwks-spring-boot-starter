@file:Suppress("unused", "UnstableApiUsage")

plugins {
    id("java")
    id("jvm-test-suite")
    id("jacoco-report-aggregation")
    id("org.owasp.dependencycheck") version "12.2.0"
    id("org.jreleaser") version "1.22.0" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

extra["jUnitVersion"] = "6.0.2"
extra["slf4jVersion"] = "2.0.17"
extra["jacksonVersion"] = "3.0.4"
extra["jSpecifyVersion"] = "1.0.0"
extra["nimbusJoseVersion"] = "10.7"
extra["springBootVersion"] = "4.0.2"
extra["nettyVersion"] = "4.2.9.Final"
extra["springVaultVersion"] = "4.0.0"
extra["bouncyCastleVersion"] = "1.83"
extra["tomcatEmbedVersion"] = "11.0.18"
extra["equalsVerifierVersion"] = "4.3.1"
extra["testcontainersVersion"] = "1.21.4"
extra["springIntegrationVersion"] = "7.0.2"
extra["springVaultStarterVersion"] = "5.0.1"

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
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    dependencyManagement {
        dependencies {
            dependency("org.junit.jupiter:junit-jupiter-api:${property("jUnitVersion")}")
            dependency("org.junit.jupiter:junit-jupiter-engine:${property("jUnitVersion")}")
            dependency("io.netty:netty-codec:${property("nettyVersion")}")
            dependency("io.netty:netty-common:${property("nettyVersion")}")
            dependency("io.netty:netty-buffer:${property("nettyVersion")}")
            dependency("io.netty:netty-handler:${property("nettyVersion")}")
            dependency("io.netty:netty-resolver:${property("nettyVersion")}")
            dependency("io.netty:netty-transport:${property("nettyVersion")}")
            dependency("io.netty:netty-transport-native-unix-common:${property("nettyVersion")}")
//            dependency("org.apache.tomcat.embed:tomcat-embed-core:${property("tomcatEmbedVersion")}")
//            dependency("org.apache.tomcat.embed:tomcat-embed-websocket:${property("tomcatEmbedVersion")}")

            dependency("org.slf4j:jul-to-slf4j:${property("slf4jVersion")}")
            dependency("org.jspecify:jspecify:${property("jSpecifyVersion")}")
            dependency("com.nimbusds:nimbus-jose-jwt:${property("nimbusJoseVersion")}")
            dependency("org.testcontainers:vault:${property("testcontainersVersion")}")
            dependency("tools.jackson.core:jackson-core:${property("jacksonVersion")}")
            dependency("tools.jackson.core:jackson-databind:${property("jacksonVersion")}")
            dependency("org.bouncycastle:bcpkix-jdk18on:${property("bouncyCastleVersion")}")
            dependency("org.testcontainers:junit-jupiter:${property("testcontainersVersion")}")
            dependency("nl.jqno.equalsverifier:equalsverifier:${property("equalsVerifierVersion")}")
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
