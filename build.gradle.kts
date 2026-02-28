import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Versions are pinned here so every module is consistent.
    // If you want, later we can add version catalogs (libs.versions.toml).
    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.spring") version "2.0.0" apply false
    id("org.springframework.boot") version "3.3.2" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    kotlin("plugin.jpa") version "1.9.24" apply false
}

allprojects {
    group = "com.nexus"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // Override Flyway to support newer PostgreSQL patch versions.
    extra["flyway.version"] = "11.20.3"

    // Default: every module uses Java 21 toolchain
    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// Kotlin compile defaults for all Kotlin modules
subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "21"
            freeCompilerArgs += listOf("-Xjsr305=strict")
        }
    }
}
