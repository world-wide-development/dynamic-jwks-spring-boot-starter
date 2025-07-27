plugins {
    id("java-library")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.encoding("UTF-8")
}

dependencies {
    api(project(":dynamic-vault-jwks"))
    api(project(":dynamic-vault-jwks-spring-boot"))
}
