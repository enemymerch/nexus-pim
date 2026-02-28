plugins {
  id("org.springframework.boot")
  id("io.spring.dependency-management")
  kotlin("jvm")
  kotlin("plugin.spring")
  kotlin("plugin.jpa")
}

dependencies {
  implementation(project(":libs:shared-kernel"))
  implementation(project(":libs:event-contracts"))

  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-validation")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.postgresql:postgresql")
  implementation("org.flywaydb:flyway-core")
  implementation("org.flywaydb:flyway-database-postgresql")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
}
