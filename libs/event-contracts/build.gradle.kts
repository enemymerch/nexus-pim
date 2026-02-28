plugins {
    kotlin("jvm")
    id("io.spring.dependency-management")
}

dependencies {
    // Event schemas / DTOs that multiple services share
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
