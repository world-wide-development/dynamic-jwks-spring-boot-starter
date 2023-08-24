plugins {
    id("java")
    id("signing")
    id("maven-publish")
    id("io.spring.dependency-management") version "1.1.2"
    id("org.springframework.boot") version "3.1.2" apply false
}

extra["nimbusJoseVersion"] = "9.31"
extra["springVaultVersion"] = "3.0.2"
extra["springCloudVersion"] = "2022.0.4"

tasks.jar {
    enabled = false
}

tasks.publish {
    enabled = false
}

tasks.publishToMavenLocal {
    enabled = false
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "signing")
    apply(plugin = "maven-publish")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    version = "0.0.4"
    group = "io.github.world-wide-development"

    repositories {
        mavenCentral()
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    tasks.javadoc {
        options.encoding("UTF-8")
    }

    signing {
        sign(publishing.publications)
        useInMemoryPgpKeys(System.getenv("MAVEN_GPG_PRIVATE_KEY"), System.getenv("MAVEN_GPG_PASSPHRASE"))
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

    publishing {
        repositories {
            maven {
                name = "OSSRH"
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
                val snapshotUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                val releaseUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                url = uri(if (version.toString().endsWith("SNAPSHOT", true)) snapshotUrl else releaseUrl)
            }
        }
        publications {
            register<MavenPublication>("vault-dynamic-jwks") {
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
                    name = "Dynamic JWKS Spring Boot Starter"
                    developers {
                        developer {
                            id = "serhey"
                            name = "Serhey Doroshenko"
                            organization = "World Wide Development"
                            email = "serhey.doroshenko.work@gmail.com"
                        }
                    }
                    licenses {
                        license {
                            name = "The Apache License, Version 2.0"
                            url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        }
                    }
                    url = "https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter"
                    description = "Dynamic JWKS Spring Boot Starter developed by World Wide Development"
                    scm {
                        url = "https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter"
                        // @formatter:off
                        connection = "scm:git:git://github.com:world-wide-development/dynamic-jwks-spring-boot-starter.git"
                        developerConnection = "scm:git:ssh://git@github.com:world-wide-development/dynamic-jwks-spring-boot-starter.git"
                        // @formatter:on
                    }
                }
            }
        }
    }

}

