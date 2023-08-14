plugins {
    id("io.spring.dependency-management") version "1.1.2"
    id("org.springframework.boot") version "3.1.2" apply false
}

extra["nimbusJoseVersion"] = "9.31"
extra["springVaultVersion"] = "3.0.2"
extra["springCloudVersion"] = "2022.0.4"

repositories {
    mavenCentral()
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    version = "0.0.7"
    group = "org.development.wide.world.spring"

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        dependencies {
            dependency("com.nimbusds:nimbus-jose-jwt:${property("nimbusJoseVersion")}")
            dependency("org.springframework.vault:spring-vault-core:${property("springVaultVersion")}")
        }
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

}

