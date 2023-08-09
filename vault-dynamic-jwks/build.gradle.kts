@file:Suppress("UnstableApiUsage")

plugins {
    id("maven-publish")
    id("jvm-test-suite")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.bootJar {
    enabled = false
}

tasks.javadoc {
    options.encoding("UTF-8")
}

tasks.check {
    dependsOn(testing.suites.named("integrationTest"))
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            testType.set(TestSuiteType.INTEGRATION_TEST)
//            targets { all { testTask.configure { shouldRunAfter(test) } } }
        }
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

dependencies {
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.vault:spring-vault-core")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    /* Unit Test */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    /* Integration Test */
    integrationTestImplementation(project)
    integrationTestImplementation("org.testcontainers:vault")
    integrationTestImplementation("org.testcontainers:junit-jupiter")
    integrationTestImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationTestImplementation("org.springframework.boot:spring-boot-testcontainers")
    integrationTestImplementation("org.springframework.cloud:spring-cloud-starter-vault-config")
}

publishing {
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
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url =
                uri("https://maven.pkg.github.com/world-wide-development/vault-dynamic-jwks-spring-boot-starter-project")
            credentials {
                password = findProperty("git.hub.packages.token") as String? ?: System.getenv("GIT_HUB_PACKAGES_TOKEN")
                username = findProperty("git.hub.packages.username") as String? ?: System.getenv("GIT_HUB_PACKAGES_USERNAME")
            }
        }
    }

}
