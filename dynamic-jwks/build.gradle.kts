@file:Suppress("unused", "UnstableApiUsage")

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options.encoding("UTF-8")
}

dependencies {
    implementation("org.jspecify:jspecify")
    implementation("org.slf4j:jul-to-slf4j")
    implementation("com.nimbusds:nimbus-jose-jwt")
    implementation("org.bouncycastle:bcpkix-jdk18on")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation("nl.jqno.equalsverifier:equalsverifier")
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation("com.nimbusds:nimbus-jose-jwt")
                implementation("org.springframework.boot:spring-boot-starter-test")
            }
        }
    }
}
