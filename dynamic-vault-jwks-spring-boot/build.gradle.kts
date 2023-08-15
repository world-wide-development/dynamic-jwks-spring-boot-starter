tasks.bootJar {
    enabled = false
}

dependencies {
    implementation(project(":dynamic-jwks"))
    compileOnly("com.nimbusds:nimbus-jose-jwt")
    compileOnly("org.springframework.vault:spring-vault-core")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    /* Test */
    testImplementation("com.nimbusds:nimbus-jose-jwt")
    testImplementation("org.springframework.vault:spring-vault-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}
