plugins {
    id("maven-publish")
}

tasks.bootJar {
    enabled = false
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.encoding("UTF-8")
}

dependencies {
    implementation(project(":vault-dynamic-jwks"))
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.springframework.vault:spring-vault-core")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    /* Test */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        register<MavenPublication>("vault-dynamic-jwks-spring-boot") {
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

