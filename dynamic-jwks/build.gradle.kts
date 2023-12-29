@file:Suppress("UnstableApiUsage")
plugins {
    id("signing")
    id("maven-publish")
    id("jvm-test-suite")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.encoding("UTF-8")
}

tasks.check {
    dependsOn(testing.suites.named("integrationTest"))
}

signing {
    sign(publishing.publications)
    useInMemoryPgpKeys(System.getenv("MAVEN_GPG_PRIVATE_KEY"), System.getenv("MAVEN_GPG_PASSPHRASE"))
}

dependencies {
    implementation("org.slf4j:jul-to-slf4j")
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.springframework:spring-core")
    implementation("org.bouncycastle:bcpkix-jdk18on")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            testType.set(TestSuiteType.UNIT_TEST)
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            testType.set(TestSuiteType.INTEGRATION_TEST)
            targets { all { testTask.configure { shouldRunAfter(test) } } }
            dependencies {
                implementation(project())
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
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
                name = "Dynamic JWKS"
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
