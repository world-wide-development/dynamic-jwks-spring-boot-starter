plugins {
    id("maven-publish")
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

dependencies {
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.vault:spring-vault-core")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    /* Test */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
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
            url = uri("https://maven.pkg.github.com/world-wide-development/vault-dynamic-jwks-spring-boot-starter-project")
            credentials {
                password = findProperty("git.hub.packages.token") as String? ?: System.getenv("GIT_HUB_PACKAGES_TOKEN")
                username = findProperty("git.hub.packages.username") as String? ?: System.getenv("GIT_HUB_PACKAGES_USERNAME")
            }
        }
    }
}
