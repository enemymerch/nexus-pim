# FULL_ARTICLE

## Title
**Building a PIM System (Part 4): A Kotlin + Spring Boot 3 Microservice Skeleton**

## Subtitle
**The real service template in Nexus PIM: shared backbone, clear boundaries, faster delivery**

## Introduction (Connecting to Part 3)
In Part 3, we clarified bounded contexts: `attribute`, `taxonomy`, and `catalog`.

This part answers a practical question: how did we make those boundaries repeatable when creating new services?

This is not theory. It is a walkthrough of the actual skeleton in the repository.

## Why the skeleton matters
Finding DDD boundaries is difficult.

But after that, a new risk appears: if each service starts with different setup patterns, maintenance and operations become expensive quickly.

In Nexus PIM, we use a shared Spring Boot 3 skeleton to avoid that drift.

## 1) Multi-module entry point
`settings.gradle.kts` defines service boundaries at the repository level:

```kotlin
include(
    "libs:shared-kernel",
    "libs:event-contracts",

    "services:catalog",
    "services:attribute",
    "services:taxonomy",
    "services:content",
    "services:media",
    "services:workflow",
    "services:search",
)
```

This gives us one monorepo with independently deployable services.

## 2) Shared Gradle baseline
Root `build.gradle.kts` keeps core versions and toolchain consistent:

- Kotlin `2.0.0`
- Spring Boot `3.3.2`
- Java toolchain `21`
- Kotlin `jvmTarget = 21`

This prevents version drift across teams and services.

## 3) Service build template
`attribute`, `taxonomy`, and `catalog` share the same build skeleton:

```kotlin
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
}
```

Domains differ, but runtime fundamentals stay identical.

## 4) Entrypoint: minimal and consistent
Each service starts with the same Spring Boot pattern:

```kotlin
@SpringBootApplication
class CatalogApplication

fun main(args: Array<String>) {
    runApplication<CatalogApplication>(*args)
}
```

The same structure exists in `AttributeApplication` and `TaxonomyApplication`.

## 5) Package layout: one shared structure
All three services use the same top-level package layout:

```text
service
|-- application
|-- domain
|-- infrastructure
`-- web
```

This removes friction for contributors. Engineers can jump between services without relearning project structure.

## 6) Runtime config: independent services, controlled integration
Each service has its own port and database:

- `catalog`: `8081` / `nexus_catalog`
- `attribute`: `8082` / `nexus_attribute`
- `taxonomy`: `8083` / `nexus_taxonomy`

`catalog` and `taxonomy` also define integration URLs:

```yaml
integration:
  attribute:
    base-url: http://localhost:8082
  taxonomy:
    base-url: http://localhost:8083
```

Practical meaning: no shared database, only API-level integration.

## 7) Web layer template: thin controllers + shared error contract
`catalog` controller example:

```kotlin
@RestController
@RequestMapping("/api/catalog/products")
class ProductController(
    private val productService: ProductService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@Valid @RequestBody req: CreateProductRequest) =
        productService.createProduct(...).toResponse()
}
```

Controllers stay transport-focused.

Error handling is also standardized:

```kotlin
@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException::class)
    fun notFound(ex: NoSuchElementException) = ... // 404

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(ex: IllegalArgumentException) = ... // 400
}
```

That keeps API behavior predictable across services.

## 8) Operational minimum: ping + actuator
Services expose lightweight liveness checks.

Example (`attribute`):

```kotlin
@RestController
class PingController {
    @GetMapping("/api/attribute/ping")
    fun ping(): Map<String, String> =
        mapOf("service" to "attribute", "status" to "ok")
}
```

Actuator `health` and `info` are enabled as well.

## 9) Persistence starter set
At skeleton level, persistence choices are fixed:

- Spring Data JPA
- PostgreSQL
- Flyway

Services bootstrap with migrations (`V1__init.sql`, and `V2` where needed). Schema evolution stays explicit in git history.

## 10) Service-to-service communication in current code
Current implementation uses REST clients between contexts:

```text
[Catalog] ----HTTP----> [Taxonomy]
    |
    +----HTTP---------> [Attribute]

[Taxonomy] ---HTTP----> [Attribute]
```

Concrete classes:

- `catalog.infrastructure.client.TaxonomyServiceClient`
- `catalog.infrastructure.client.AttributeServiceClient`
- `taxonomy.infrastructure.client.AttributeServiceClient`

The skeleton enforces a key boundary rule: call service contracts, not service databases.

## 11) Testing baseline: context smoke tests
Each service includes a minimal but useful startup test:

```kotlin
@SpringBootTest
class ApplicationSmokeTest {
    @Test
    fun contextLoads() {
        // if the context fails to start, this test will fail
    }
}
```

Small test, high value for CI stability.

## 12) What exists now vs what comes next
This article focuses on what is already implemented in the repository:

- Shared Gradle baseline
- Standard package layout
- Separate DB / separate port per service
- REST integration client pattern

Roadmap/README topics like full event backbone depth and advanced search projection are beyond this skeleton article and belong to later parts.

## 13) Conclusion
Creating a Spring Boot microservice is easy.

Creating many services that stay consistent is hard.

In this project, consistency comes from a repeatable skeleton:

- same build template
- same package language
- same web/error/test baseline
- clear boundary discipline (DB ownership + API integration)

That standardization lets the team spend more time on domain behavior and less on framework setup.

## 14) Teaser for Part 5
In Part 5, we’ll go deep on `Variant (SKU)` design: combination integrity, signature strategy, and what breaks when SKU modeling is done poorly.

---

# SHORTER_VERSION

## Title
**Part 4: Kotlin + Spring Boot 3 Microservice Skeleton (from the actual Nexus PIM repo)**

Part 3 defined bounded contexts. Part 4 shows the implementation skeleton that keeps those contexts consistent in code.

Repository-level skeleton:

- Multi-module Gradle (`services:attribute`, `services:taxonomy`, `services:catalog`, ...)
- Shared version/toolchain baseline (Kotlin 2.0, Spring Boot 3.3.2, Java 21)
- Shared dependency set (web, validation, actuator, jpa, flyway, postgres)
- Standard package layout: `application/domain/infrastructure/web`

Entrypoint pattern:

```kotlin
@SpringBootApplication
class AttributeApplication

fun main(args: Array<String>) {
    runApplication<AttributeApplication>(*args)
}
```

API baseline:

- Thin controllers
- `@RestControllerAdvice` for consistent error responses
- Ping endpoints + actuator health/info

Config baseline:

- Separate port per service
- Separate DB per service (`nexus_attribute`, `nexus_taxonomy`, `nexus_catalog`)
- Integration base URLs for service calls

Current communication flow:

```text
Catalog -> Taxonomy
Catalog -> Attribute
Taxonomy -> Attribute
```

Testing baseline:

- `@SpringBootTest` context smoke test in each service

Takeaway: this skeleton removes repeated setup decisions so teams can focus on domain rules.

---

# TITLE_ALTERNATIVES

1. **Building a PIM System (Part 4): A Practical Kotlin + Spring Boot Microservice Skeleton**
2. **From Bounded Context to Running Service: Spring Boot 3 Skeleton in Practice**
3. **Standardizing Microservice Startup in Nexus PIM (Kotlin + Spring Boot)**

---

# SUBTITLE_ALTERNATIVES

1. **Real module, package, config and API patterns from the repository**
2. **How we keep new services consistent before adding domain complexity**
3. **A repeatable service template for Attribute, Taxonomy and Catalog contexts**

---

# LINKEDIN_POST

Part 4 of my “Building a PIM System” series is out.

This chapter covers the Kotlin + Spring Boot 3 microservice skeleton behind the project.

I walk through the actual repository setup:
- Multi-module Gradle structure
- Shared service baseline (web, jpa, flyway, actuator)
- Consistent package layout (`application/domain/infrastructure/web`)
- Standard API/error/ping patterns
- Per-service DB ownership + integration client boundaries

Goal: new services should start fast and stay consistent, so engineering time goes to domain logic instead of repeated framework setup.

#kotlin #springboot #microservices #backend #architecture #gradle #pim

---

# COVER_IMAGE_PROMPT

Minimal technical Medium cover, white background, blue and gray palette, clean flat vector style, central title-free hero with three service blocks (Attribute, Taxonomy, Catalog), layered bands labeled web/application/domain/infrastructure, thin arrows between services, subtle Spring and Kotlin visual cues, lots of whitespace, crisp modern editorial look, no people, no dark mode, 16:9
