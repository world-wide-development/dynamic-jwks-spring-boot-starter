tasks.bootJar {
    enabled = false
}

dependencies {
    implementation(project(":dynamic-jwks"))
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
