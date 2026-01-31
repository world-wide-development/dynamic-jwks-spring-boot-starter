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
    implementation(project(":dynamic-jwks"))
    implementation("org.slf4j:jul-to-slf4j")
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("tools.jackson.core:jackson-databind")
    implementation("org.springframework.data:spring-data-redis")
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
                implementation(project(":dynamic-jwks"))
                implementation("org.jspecify:jspecify")
                implementation("com.nimbusds:nimbus-jose-jwt")
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
        active.set(Active.ALWAYS)
        pgp {
            armored.set(true)
            publicKey.set("${System.getenv("MAVEN_GPG_PUBLIC_KEY") ?: findProperty("gpg.public.key")}")
            secretKey.set("${System.getenv("MAVEN_GPG_PRIVATE_KEY") ?: findProperty("gpg.private.key")}")
            passphrase.set("${System.getenv("MAVEN_GPG_PASSPHRASE") ?: findProperty("gpg.key.passphrase")}")
        }
    }
    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    active.set(Active.ALWAYS)
                    stagingRepository("build/staging-deploy")
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    username.set("${System.getenv("MAVEN_USERNAME") ?: findProperty("sonatype.maven.username")}")
                    password.set("${System.getenv("MAVEN_PASSWORD") ?: findProperty("sonatype.maven.password")}")
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
        register<MavenPublication>("dynamic-redis-jwks") {
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
                inceptionYear.set("2023")
                name.set("Dynamic Redis JWKS")
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
