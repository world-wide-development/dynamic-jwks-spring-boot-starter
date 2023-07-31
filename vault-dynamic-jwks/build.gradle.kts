dependencies {
    implementation("org.springframework.vault:spring-vault-core")
    /* Test */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}
