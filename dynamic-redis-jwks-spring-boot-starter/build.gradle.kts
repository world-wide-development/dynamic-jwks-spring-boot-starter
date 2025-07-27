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
    api(project(":dynamic-redis-jwks"))
    api(project(":dynamic-redis-jwks-spring-boot"))
}
