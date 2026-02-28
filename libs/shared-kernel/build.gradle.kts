plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
}

dependencies {
    // Shared: common utils, errors, ids, etc.
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}
