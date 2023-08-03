plugins {
    id("java-library")
    id("maven-publish")
}

tasks.bootJar {
    enabled = false
}

dependencies {
    api(project(":vault-dynamic-jwks"))
    api(project(":vault-dynamic-jwks-spring-boot"))
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.encoding("UTF-8")
}

publishing {
    publications {
        register<MavenPublication>("vault-dynamic-jwks-spring-boot-starter") {
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
