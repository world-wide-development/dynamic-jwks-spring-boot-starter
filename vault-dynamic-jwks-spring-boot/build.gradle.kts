dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    /* Test */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}

