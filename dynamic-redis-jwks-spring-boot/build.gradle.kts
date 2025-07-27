@file:Suppress("unused", "UnstableApiUsage")

import org.jreleaser.model.Active
import org.jreleaser.model.Stereotype

plugins {
    id("org.jreleaser")
}

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

jreleaser {
    release {
        github {
            enabled.set(false)
        }
    }
    signing {
        armored.set(true)
        active.set(Active.ALWAYS)
        passphrase.set(System.getenv("GPG_PASSPHRASE"))
        publicKey.set(System.getenv("MAVEN_GPG_PUBLIC_KEY"))
        secretKey.set(System.getenv("MAVEN_GPG_PRIVATE_KEY"))
    }
    deploy {
        maven {
            mavenCentral {
                create("dynamic-redis-jwks-spring-boot") {
                    active.set(Active.ALWAYS)
                    stagingRepository("target/staging-deploy")
                    username.set(System.getenv("MAVEN_USERNAME"))
                    password.set(System.getenv("MAVEN_PASSWORD"))
                    url.set("https://central.sonatype.com/api/v1/publisher")
                }
            }
        }
    }
    project {
        license.set("Apache-2.0")
        inceptionYear.set("2023")
        stereotype.set(Stereotype.WEB)
        maintainers.add("Serhey Doroshenko")
        vendor.set("World Wide Development")
        version.set("${rootProject.version}")
        copyright.set("2023 Serhey Doroshenko")
        name.set("dynamic-redis-jwks-spring-boot")
        tags.set(listOf("jwks", "dynamic-jwks", "spring-boot"))
        description.set("Dynamic JWKS Spring Boot Starter developed by World Wide Development")
        links {
            homepage.set("https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter")
            documentation.set("${homepage}/blob/release/0.1.x/README.md")
            license.set("${homepage}/blob/release/0.1.x/LICENSE")
        }
    }
}
