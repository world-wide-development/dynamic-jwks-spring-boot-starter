dependencies {
    implementation(project(":vault-dynamic-jwks-spring-boot"))
}

tasks.bootJar {
    enabled = false
}
