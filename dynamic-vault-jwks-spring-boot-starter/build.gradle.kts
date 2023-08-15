plugins {
    id("java-library")
}

tasks.bootJar {
    enabled = false
}

dependencies {
    api(project(":dynamic-jwks"))
    api(project(":dynamic-vault-jwks-spring-boot"))
}
