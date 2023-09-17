dependencies {
    implementation("com.fasterxml.jackson:jackson-base:2.14.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.1")
}
tasks.jar {
    enabled = false
}

tasks.bootJar {
    enabled = false
}

tasks.resolveMainClassName {
    enabled = false
}
