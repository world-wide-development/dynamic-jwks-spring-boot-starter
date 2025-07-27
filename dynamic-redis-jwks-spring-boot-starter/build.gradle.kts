import org.jreleaser.model.Active
import org.jreleaser.model.Stereotype

plugins {
    id("java-library")
    id("org.jreleaser")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.encoding("UTF-8")
}

dependencies {
    api(project(":dynamic-redis-jwks"))
    api(project(":dynamic-redis-jwks-spring-boot"))
}

jreleaser {
    signing {
        armored.set(true)
        active.set(Active.ALWAYS)
        passphrase.set(System.getenv("GPG_PASSPHRASE"))
        publicKey.set(System.getenv("MAVEN_GPG_PUBLIC_KEY"))
        secretKey.set(System.getenv("MAVEN_GPG_PRIVATE_KEY"))
    }
    deploy {
        maven {
            mavenCentral {
                create("dynamic-redis-jwks-spring-boot-starter") {
                    active.set(Active.ALWAYS)
                    stagingRepository("target/staging-deploy")
                    username.set(System.getenv("MAVEN_USERNAME"))
                    password.set(System.getenv("MAVEN_PASSWORD"))
                    url.set("https://central.sonatype.com/api/v1/publisher")
                }
            }
        }
    }
    project {
        license.set("Apache-2.0")
        inceptionYear.set("2023")
        stereotype.set(Stereotype.WEB)
        maintainers.add("Serhey Doroshenko")
        vendor.set("World Wide Development")
        version.set("${rootProject.version}")
        copyright.set("2023 Serhey Doroshenko")
        name.set("Dynamic Redis JWKS Spring Boot Starter")
        tags.set(listOf("jwks", "dynamic-jwks", "spring-boot"))
        description.set("Dynamic JWKS Spring Boot Starter developed by World Wide Development")
        links {
            homepage.set("https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter")
            documentation.set("${homepage}/blob/release/0.1.x/README.md")
            license.set("${homepage}/blob/release/0.1.x/LICENSE")
        }
    }
}
