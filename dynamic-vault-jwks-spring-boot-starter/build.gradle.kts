import org.jreleaser.model.Active

plugins {
    id("java-library")
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
    api(project(":dynamic-vault-jwks"))
    api(project(":dynamic-vault-jwks-spring-boot"))
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
        register<MavenPublication>("dynamic-vault-jwks-spring-boot-starter") {
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
                name.set("Dynamic Vault JWKS Spring Boot Starter")
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
