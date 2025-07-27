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
    implementation("org.slf4j:jul-to-slf4j")
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.data:spring-data-redis")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.springframework.integration:spring-integration-core")
    implementation("org.springframework.integration:spring-integration-redis")
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
                implementation("com.nimbusds:nimbus-jose-jwt")
                implementation(project(":dynamic-jwks"))
                implementation("org.testcontainers:junit-jupiter")
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.springframework.boot:spring-boot-testcontainers")
                implementation("org.springframework.boot:spring-boot-starter-data-redis")
                implementation("org.springframework.integration:spring-integration-core")
                implementation("org.springframework.integration:spring-integration-redis")
            }
        }
    }
}

jreleaser {
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
                create("dynamic-redis-jwks") {
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
        name.set("Dynamic Redis JWKS")
        stereotype.set(Stereotype.WEB)
        maintainers.add("Serhey Doroshenko")
        vendor.set("World Wide Development")
        version.set("${rootProject.version}")
        copyright.set("2023 Serhey Doroshenko")
        tags.set(listOf("jwks", "dynamic-jwks", "spring-boot"))
        description.set("Dynamic JWKS Spring Boot Starter developed by World Wide Development")
        links {
            homepage.set("https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter")
            documentation.set("${homepage}/blob/release/0.1.x/README.md")
            license.set("${homepage}/blob/release/0.1.x/LICENSE")
        }
    }
}
