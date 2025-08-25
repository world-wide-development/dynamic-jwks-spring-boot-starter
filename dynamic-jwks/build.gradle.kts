@file:Suppress("unused", "UnstableApiUsage")

import org.jreleaser.model.Active

plugins {
    id("org.jreleaser")
    id("maven-publish")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Javadoc> {
    options {
        this as CoreJavadocOptions
        encoding("UTF-8")
        addStringOption("Xdoclint:none", "-quiet")
    }
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
    gitRootSearch.set(true)
    release {
        github {
            sign.set(false)
            skipTag.set(true)
            token.set("no-op")
            skipRelease.set(true)
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
                register("sonatype") {
                    active.set(Active.ALWAYS)
                    stagingRepository("build/staging-deploy")
                    username.set(System.getenv("MAVEN_USERNAME"))
                    password.set(System.getenv("MAVEN_PASSWORD"))
                    url.set("https://central.sonatype.com/api/v1/publisher")
                }
            }
        }
    }
}

publishing {
    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
    publications {
        register<MavenPublication>("dynamic-jwks") {
            from(components["java"])
            versionMapping {
                usage("java-runtime") {
                    fromResolutionResult()
                }
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
            }
            pom {
                name.set("Dynamic JWKS")
                inceptionYear.set("2023")
                developers {
                    developer {
                        id.set("serhey")
                        timezone.set("Europe/Kyiv")
                        name.set("Serhey Doroshenko")
                        organization.set("World Wide Development")
                        email.set("serhey.doroshenko.work@gmail.com")
                    }
                }
                organization {
                    name.set("World Wide Development")
                    url.set("https://github.com/world-wide-development")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                url.set("https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter")
                description.set("Dynamic JWKS Spring Boot Starter developed by World Wide Development")
                scm {
                    tag.set("dynamic-jwks")
                    url.set("https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter")
                    // @formatter:off
                    connection.set("scm:git:git://github.com:world-wide-development/dynamic-jwks-spring-boot-starter.git")
                    developerConnection.set("scm:git:ssh://git@github.com:world-wide-development/dynamic-jwks-spring-boot-starter.git")
                    // @formatter:on
                }
            }
        }
    }
}
