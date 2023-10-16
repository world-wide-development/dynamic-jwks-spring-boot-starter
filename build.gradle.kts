plugins {
    id("java")
    id("org.owasp.dependencycheck") version "8.4.0"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.springframework.boot") version "3.1.4" apply false
}

tasks.jar {
    enabled = false
}

extra["nimbusJoseVersion"] = "9.36"
extra["springVaultVersion"] = "3.0.4"
extra["bouncyCastleVersion"] = "1.76"
extra["springCloudVersion"] = "2022.0.4"

subprojects {

    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "org.owasp.dependencycheck")
    apply(plugin = "io.spring.dependency-management")

    version = "0.0.8"
    group = "io.github.world-wide-development"

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
            dependency("org.yaml:snakeyaml:2.2")
            dependency("com.fasterxml.jackson.core:jackson-databind:2.15.3")
            dependency("com.nimbusds:nimbus-jose-jwt:${property("nimbusJoseVersion")}")
            dependency("org.bouncycastle:bcpkix-jdk18on:${property("bouncyCastleVersion")}")
            dependency("org.springframework.vault:spring-vault-core:${property("springVaultVersion")}")
        }
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

}
