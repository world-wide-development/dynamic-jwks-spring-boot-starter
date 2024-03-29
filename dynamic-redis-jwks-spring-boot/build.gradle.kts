@file:Suppress("UnstableApiUsage", "GradlePackageUpdate", "GradlePackageVersionRange")

plugins {
    id("signing")
    id("maven-publish")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.encoding("UTF-8")
}

dependencies {
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
            testType.set(TestSuiteType.UNIT_TEST)
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            testType.set(TestSuiteType.INTEGRATION_TEST)
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
    }
}

signing {
    sign(publishing.publications)
    useInMemoryPgpKeys(System.getenv("MAVEN_GPG_PRIVATE_KEY"), System.getenv("MAVEN_GPG_PASSPHRASE"))
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
        register<MavenPublication>("dynamic-redis-jwks-spring-boot") {
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
                name = "Dynamic Redis JWKS Spring Boot"
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
