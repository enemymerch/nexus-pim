## 1) Title
**Building a PIM System (Part 2): Clean Architecture for Maintainable Microservices**

## 2) Subtitle
**How we structured Kotlin + Spring Boot services so domain logic stayed clean as the PIM platform grew**

## 3) Short Introduction (Connecting to Part 1)
In Part 1, I argued that splitting into microservices too early usually creates distributed complexity, not real scalability.

Part 2 is about what worked for us before service boundaries became stable: clean internal architecture.

In our PIM project, service boundaries (Attribute, Taxonomy, Catalog) became clearer only after we separated domain logic from delivery and infrastructure details. Clean Architecture was not a "pattern exercise." It was a way to keep change cheap.

---

## 4) Why Architecture Matters
In a PIM system, product data changes constantly:

- New attribute types
- New category trees
- New validation rules by market
- New integration contracts with commerce channels

If business logic is mixed with controllers, ORM entities, and messaging code, every change becomes risky.

What we needed in practice:

- Domain rules that can be tested without HTTP or DB
- Use cases that express business intent clearly
- Infrastructure that can change (PostgreSQL/Kafka/Redis) without rewriting core logic

That reduced regression risk and made refactoring possible while the platform was still evolving.

---

## 5) The Core Idea of Clean Architecture
The core idea is simple: **business rules should not depend on technical details**.

In our services, the domain model is the center. Frameworks and adapters are outer layers.

That gives us two practical benefits:

- Business logic remains stable while APIs/infrastructure evolve
- Service boundaries become easier to identify because use cases are explicit

---

## 6) The Dependency Rule
Dependencies should point inward, toward the domain.

Minimal dependency direction:

```text
API / Controller
      |
      v
Application (Use Cases)
      |
      v
Domain
      ^
      |
Infrastructure (DB, Kafka, Redis adapters)
```

Domain knows nothing about Spring, JPA, Kafka, or HTTP.

Infrastructure depends on domain contracts (ports), not the other way around.

---

## 7) Clean Architecture Layers

### Domain Layer
Contains core business objects and rules.

In `attribute-service`, this includes `Attribute`, enums, and repository contracts.  
No Spring annotations. No persistence annotations. No web concerns.

### Application Layer
Contains application services like `AttributeService`.

This layer orchestrates domain behavior and enforces application-level policies (validation flow, uniqueness checks, transaction boundaries if needed).

### Infrastructure Layer
Contains implementations of technical concerns:

- PostgreSQL repositories
- Kafka publishers
- Redis caching adapters
- Spring configuration

This layer is replaceable. It should not own business rules.

### API / Controller Layer
Contains HTTP entry points and request/response models.

Controllers map external payloads to use case commands and return responses.  
They should stay thin.

---

## 8) Applying Clean Architecture to a PIM Service
Let’s take `attribute-service`.

When a client creates an attribute like `color` or `material`, the flow is:

```text
Client -> AttributeController -> AttributeService -> Attribute (domain) -> AttributeStorePort
```

Request flow diagram:

```text
[Client]
   |
   v
[Controller]
   |
   v
[Application Service]
   |
   v
[Domain Model]
   |
   v
[Store Port]
   |
   v
[PostgreSQL Adapter]
```

And the project structure:

```text
attribute-service
|-- domain
|   |-- model
|   |-- enums
|   `-- port
|-- application
|   `-- service
|-- infrastructure
|   |-- adapter
|   |-- persistence
|   `-- config
`-- web
    |-- controller
    `-- dto
```

This structure kept our services understandable even as we added async events and caching.

---

## 9) Practical Kotlin + Spring Boot Code Examples

### 9.1 Domain Model Example (`Attribute`)

```kotlin
package com.nexus.attribute.domain.model

import com.nexus.attribute.domain.enums.AttributeDataType
import com.nexus.attribute.domain.enums.AttributeScope
import java.time.Instant

class Attribute private constructor(
    val code: String,
    val dataType: AttributeDataType,
    val scope: AttributeScope,
    val localizable: Boolean,
    val channelOverridable: Boolean,
    val multiValue: Boolean,
    val createdAt: Instant
    ){
    companion object{
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")

        fun create(
            code: String,
            dataType: AttributeDataType,
            scope: AttributeScope,
            localizable: Boolean,
            channelOverridable: Boolean,
            multiValue: Boolean,
            now: Instant = Instant.now()
        ) : Attribute {
            val normalizedCode = normalizeCode(code)
            validateCode(normalizedCode)
            return Attribute(code = normalizedCode, dataType = dataType, scope = scope, localizable = localizable, channelOverridable = channelOverridable, multiValue = multiValue, createdAt =  now)
        }

        private fun validateCode(code: String) {
            require(code.isNotBlank()) { "Attribute code cannot be blank." }
            require(CODE_REGEX.matches(code)) {
                "Invalid attribute code '$code'. Use 2-64 chars: lowercase letters, numbers, '_' or '-'."
            }
        }

        fun normalizeCode(code: String): String = code.trim().lowercase()


    }
    override fun toString(): String =
        "Attribute(code='$code', dataType=$dataType, scope=$scope, localizable=$localizable, channelOverridable=$channelOverridable, multiValue=$multiValue)"
}
```

This model contains business invariants directly. It does not depend on Spring or JPA.

---

### 9.2 Repository Interface Example (`AttributeStorePort`)

```kotlin
package com.nexus.attribute.domain.port

import com.nexus.attribute.domain.model.Attribute

interface AttributeStorePort {
    fun save(attribute: Attribute): Attribute
    fun findByCode(code: String): Attribute?
    fun existsByCode(code: String): Boolean
    fun findAll(): List<Attribute>
}
```

This is a domain port. The domain/application layers depend on this interface, while infrastructure provides the implementation.

---

### 9.3 Application Use Case Example (`AttributeService`)

```kotlin
package com.nexus.attribute.application.service

import com.nexus.attribute.domain.enums.AttributeDataType
import com.nexus.attribute.domain.enums.AttributeScope
import com.nexus.attribute.domain.model.Attribute
import com.nexus.attribute.domain.port.AttributeStorePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AttributeService(
    private val store: AttributeStorePort
) {

    @Transactional
    fun create(
        code: String,
        dataType: AttributeDataType,
        scope: AttributeScope,
        localizable: Boolean,
        channelOverridable: Boolean,
        multiValue: Boolean
    ): Attribute {
        val normalized = Attribute.normalizeCode(code)

        if (store.existsByCode(normalized)) {
            throw IllegalArgumentException("Attribute already exists: $normalized")
        }

        val attr = Attribute.create(
            code = normalized,
            dataType = dataType,
            scope = scope,
            localizable = localizable,
            channelOverridable = channelOverridable,
            multiValue = multiValue
        )

        return store.save(attr)
    }

    @Transactional(readOnly = true)
    fun getByCode(code: String): Attribute =
        store.findByCode(code)
            ?: throw NoSuchElementException("Attribute not found: ${code.trim().lowercase()}")

    @Transactional(readOnly = true)
    fun list(scope: AttributeScope?): List<Attribute> =
        store.findAll().filter { scope == null || it.scope == scope }
}
```

The application service orchestrates business flow: normalize code, enforce uniqueness, create domain object, and persist through the port.

---

### 9.4 REST Controller Example (`AttributeController`)

```kotlin
package com.nexus.attribute.web.controller

import com.nexus.attribute.application.service.AttributeService
import com.nexus.attribute.domain.enums.AttributeScope
import com.nexus.attribute.web.dto.CreateAttributeRequest
import com.nexus.attribute.web.dto.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/attribute/attributes")
class AttributeController(
    private val service: AttributeService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateAttributeRequest) =
        service.create(
            code = req.code,
            dataType = req.dataType,
            scope = req.scope,
            localizable = req.localizable,
            channelOverridable = req.channelOverridable,
            multiValue = req.multiValue
        ).toResponse()

    @GetMapping("/{code}")
    fun get(@PathVariable code: String) =
        service.getByCode(code).toResponse()

    @GetMapping
    fun list(@RequestParam(required = false) scope: AttributeScope?) =
        service.list(scope).map { it.toResponse() }
}
```

The controller stays thin: it validates transport input, delegates to application service, and maps domain output to DTO.

---

## 10) Common Mistakes Teams Make
The recurring issues we saw:

- Treating entities as JPA-first models instead of domain models
- Putting business rules in controllers/services with framework-heavy code
- Using “microservice” as a deployment decision, not a domain boundary decision
- Sharing database schemas across services
- Introducing Kafka events before use-case boundaries are clear
- Building packages by technical type only (`controller/service/repository`) without domain intent

Most of these are symptoms of one thing: weak internal architecture.

---

## 11) Conclusion
Clean Architecture did not magically solve complexity. But it made complexity explicit and local.

In this PIM system, maintainability improved because domain logic stayed isolated, use cases were clear, and infrastructure remained replaceable.

The key lesson: **good microservice boundaries emerged after each service became internally clean**. Not before.

---

## 12) Teaser for Part 3
In Part 3, I’ll go deeper into Domain Driven Design in this same PIM system:

- Defining bounded contexts for Attribute, Taxonomy, and Catalog
- Modeling aggregates like Product and Variant (SKU)
- Designing ubiquitous language that survives real team growth

---

## Shorter Optimized Version (~4-minute read)

### Title
**Building a PIM System (Part 2): Clean Architecture for Maintainable Services**

Part 1 was about why teams often misuse microservices.  
Part 2 is about what helped us before splitting further: clean service internals.

In our Kotlin + Spring Boot PIM platform, service boundaries became clearer only after we separated business logic from infrastructure details.

Clean Architecture in practice means:

- Domain rules at the center
- Use cases for business actions
- Infrastructure as adapters
- Thin controllers

Dependency direction:

```text
Controller -> Application -> Domain <- Infrastructure
```

Request flow in `attribute-service`:

```text
Client -> Controller -> AttributeService -> Domain -> AttributeStorePort
```

Project structure:

```text
attribute-service
|-- domain
|-- application
|-- infrastructure
`-- web
```

Example domain model keeps invariants close to data:

```kotlin
class Attribute private constructor(
    val code: String,
    val dataType: AttributeDataType,
    val scope: AttributeScope,
    val localizable: Boolean,
    val channelOverridable: Boolean,
    val multiValue: Boolean,
    val createdAt: Instant
)
```

Repository is a domain port:

```kotlin
interface AttributeStorePort {
    fun save(attribute: Attribute): Attribute
    fun findByCode(code: String): Attribute?
    fun existsByCode(code: String): Boolean
    fun findAll(): List<Attribute>
}
```

Application service holds the business flow:

```kotlin
class AttributeService(private val store: AttributeStorePort) {
    fun create(code: String, dataType: AttributeDataType, scope: AttributeScope, localizable: Boolean, channelOverridable: Boolean, multiValue: Boolean): Attribute {
        val normalized = Attribute.normalizeCode(code)
        if (store.existsByCode(normalized)) error("Duplicate code")
        return store.save(Attribute.create(normalized, dataType, scope, localizable, channelOverridable, multiValue))
    }
}
```

Controller stays as transport adapter:

```kotlin
@RestController
@RequestMapping("/api/attribute/attributes")
class AttributeController(private val service: AttributeService) {
    @PostMapping
    fun create(@RequestBody req: CreateAttributeRequest) =
        service.create(req.code, req.dataType, req.scope, req.localizable, req.channelOverridable, req.multiValue).toResponse()
}
```

Common mistakes:

- Framework-first domain models
- Fat controllers
- Shared DB between services
- Event-driven design before domain boundaries are stable

Takeaway: clean internals first, then microservice boundaries become much easier to define.

Part 3 will cover DDD in this PIM system: bounded contexts, aggregates, and ubiquitous language.

---

## 3 Alternative Titles
1. **PIM from Scratch (Part 2): Clean Architecture That Survives Real Change**
2. **Designing Maintainable PIM Microservices with Clean Architecture**
3. **Before More Microservices: Clean Architecture Lessons from a PIM Backend**

## 3 Alternative Subtitles
1. **Practical Kotlin + Spring Boot patterns we used to keep domain logic stable**
2. **How clean service internals helped us define better boundaries in a PIM platform**
3. **A backend engineer’s approach to layering, dependencies, and long-term maintainability**

---

## LinkedIn Post Text
Part 2 of my “Building a PIM System” series is out.

This one is about Clean Architecture in real backend services, not textbook diagrams.

I share how we structured Kotlin + Spring Boot services (Attribute, Taxonomy, Catalog) with:
- Domain-first modeling
- Use-case-driven application layer
- Thin controllers
- Replaceable infrastructure (PostgreSQL, Kafka, Redis)

Big takeaway: microservice boundaries got much clearer only after internal architecture became clean.

If you’re building commerce or product-data platforms, this is the step that prevents expensive rewrites later.

#kotlin #springboot #microservices #cleanarchitecture #ddd #backend #ecommerce #pim

---

## Medium Cover Image Prompt (AI Generator)
Minimal tech blog illustration, white background, blue and gray color palette, clean modern style, abstract backend architecture diagram with layered blocks labeled Domain, Application, Infrastructure, API, arrows showing dependency direction inward, subtle product data icons (tag, category tree, SKU box), flat vector style, lots of whitespace, professional and editorial look, suitable for Medium article cover, 16:9 ratio.

