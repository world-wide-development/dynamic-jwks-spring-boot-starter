plugins {
    id("io.spring.dependency-management") version "1.1.2"
    id("org.springframework.boot") version "3.1.2" apply false
}

extra["springVaultVersion"] = "3.0.2"

subprojects {

    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    version = "0.0.1"
    group = "org.development.wide.world.spring"

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        dependencies {
            dependency("org.springframework.vault:spring-vault-core:${property("springVaultVersion")}")
        }
    }

}

