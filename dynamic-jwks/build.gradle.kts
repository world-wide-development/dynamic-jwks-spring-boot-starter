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
    options.quiet()
    options.encoding("UTF-8")
}

dependencies {
    implementation("org.jspecify:jspecify")
    implementation("org.slf4j:jul-to-slf4j")
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.bouncycastle:bcpkix-jdk18on")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation("nl.jqno.equalsverifier:equalsverifier")
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            targets.all {
                testTask.configure {
                    failOnNoDiscoveredTests.set(false)
                }
            }
            dependencies {
                implementation(project())
                implementation("com.nimbusds:nimbus-jose-jwt")
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
        publicKey.set(System.getenv("MAVEN_GPG_PUBLIC_KEY"))
        secretKey.set(System.getenv("MAVEN_GPG_PRIVATE_KEY"))
        passphrase.set(System.getenv("MAVEN_GPG_PASSPHRASE"))
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
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
        name.set("dynamic-jwks")
        license.set("Apache-2.0")
        inceptionYear.set("2023")
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
