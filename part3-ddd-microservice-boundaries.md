# FULL_ARTICLE

## 1. Title
**Building a PIM System (Part 3): Using DDD to Define Microservice Boundaries**

## 2. Subtitle
**How our actual Kotlin codebase separated Attribute, Taxonomy, and Catalog without forcing boundaries from the database**

## 3. Introduction connecting to Part 2
Part 2 was about internal service design with Clean Architecture.

This part is about the next decision: where one service should end and another should begin.

In this repository, those boundaries did not come from org charts or table names. They came from domain behavior in code: `AttributeSet` rules in `attribute`, hierarchy logic in `taxonomy`, and product + variant rules in `catalog`.

## 4. The Problem with Arbitrary Microservices
A common failure mode is splitting by CRUD tables first, then trying to recover domain logic later.

This repo has a concrete signal of that risk. In `catalog` migration `V2__integration_refactor.sql`, the model moves from ID-based coupling (`attribute_id`, `attribute_value_id`) to code-based integration (`attribute_code`, `attribute_value_code`).

That change is a DDD-style correction: catalog keeps catalog rules, and asks other contexts for meaning through APIs.

## 5. Understanding the Domain of Product Data
At a high level, the domain in code looks like this:

```text
PIM Domain
|-- Attributes
|-- Taxonomy
`-- Catalog
```

- `Attributes` defines what data is valid.
- `Taxonomy` defines where products sit in category hierarchy.
- `Catalog` defines sellable product structures and variant combinations.

This is not theoretical. Each area has its own Spring Boot service, persistence model, and application logic.

## 6. Discovering Bounded Contexts in the PIM Domain
In this repository, bounded contexts became visible through language and invariants:

- **Attribute Context**: `Attribute`, `AttributeSet`, `AttributeGroup`, `constraintsJson`, `AttributeDataType`, `AttributeScope`.
- **Taxonomy Context**: `Category`, `path`, `depth`, `parentId`, `effectiveAttributeSetCodes`.
- **Catalog Context**: `Product`, `Variant`, `VariantOption`, `signature`, `variantAttributeCodes`.

Context relationship (current implementation):

```text
[Attribute Context] ---> [Catalog Context] <--- [Taxonomy Context]
         ^                         ^
         |                         |
         +------ [Taxonomy Context]-+
```

Catalog consumes both Attribute and Taxonomy metadata. Taxonomy also validates assigned attribute sets through Attribute service.

## 7. How the Repository Revealed the Boundaries
The boundaries are explicit in code structure and runtime config:

- Separate modules: `services:attribute`, `services:taxonomy`, `services:catalog`.
- Separate DBs in each `application.yml`:
  - `nexus_attribute`
  - `nexus_taxonomy`
  - `nexus_catalog`
- Cross-context access through REST clients:
  - `catalog.infrastructure.client.AttributeServiceClient`
  - `catalog.infrastructure.client.TaxonomyServiceClient`
  - `taxonomy.infrastructure.client.AttributeServiceClient`

That is strong domain ownership: no direct table joins across services.

## 8. Attribute Domain
`Attribute` is not just a row; it normalizes and validates domain identity (`code`) and carries type/scope semantics.

```kotlin
class Attribute private constructor(
    val code: String,
    val dataType: AttributeDataType,
    val scope: AttributeScope,
    val localizable: Boolean,
    val channelOverridable: Boolean,
    val multiValue: Boolean,
    val createdAt: Instant
) {
    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")

        fun create(...): Attribute {
            val normalizedCode = normalizeCode(code)
            validateCode(normalizedCode)
            return Attribute(code = normalizedCode, ...)
        }
    }
}
```

Architectural reading: attribute identity and validation live in the domain model, not in controllers.

`AttributeSet` behaves like an aggregate root that owns membership and consistency of groups/items.

```kotlin
class AttributeSet internal constructor(..., private val groups: MutableList<AttributeGroup>, private val items: MutableList<AttributeSetItem>, ...) {
    fun addGroup(code: String, name: String, sortOrder: Int, now: Instant = Instant.now()): AttributeGroup { ... }

    fun attachAttribute(
        attributeCode: String,
        required: Boolean,
        sortOrder: Int,
        groupCode: String?,
        constraintsJson: String?,
        now: Instant = Instant.now()
    ): AttributeSetItem {
        require(items.none { it.attributeCode == attrCode }) { "Attribute already attached: $attrCode" }
        ...
    }
}
```

Architectural reading: `AttributeGroup` and `AttributeSetItem` are modified through `AttributeSet`, which is classic aggregate consistency control.

## 9. Taxonomy Domain
Taxonomy owns hierarchy behavior, not catalog.

`Category` contains tree-specific invariants (`path`, `depth`, parent transitions):

- `createRoot`
- `createChild`
- `moveTo`

`TaxonomyService.updateCategory` also updates descendants when parent changes by path-prefix logic.

This is important for DDD boundaries: category tree mutation is expensive domain behavior and belongs in one place.

Taxonomy also owns mapping category -> attribute sets via `CategoryAttributeSet` and resolves inherited sets through lineage (`resolveEffectiveAttributeSetCodes`).

So Taxonomy does not define attributes, but it defines which attribute sets are effective for a category path.

## 10. Catalog Domain
Catalog owns sellable structure and combination rules.

`Product` acts as aggregate root and protects variant invariants:

```kotlin
class Product internal constructor(..., private val variantAttributeCodes: MutableSet<String>, private val variants: MutableSet<Variant>, ...) {
    fun setVariantAttributes(attributeCodes: Set<String>) {
        if (variants.isNotEmpty() && variantAttributeCodes != normalized) {
            throw IllegalStateException("Cannot change variant attribute set after variant creation...")
        }
        ...
    }

    fun addVariant(..., options: Set<VariantOption>, ...): Variant {
        require(optionAttrCodes.toSet() == variantAttributeCodes) {
            "Variant options must contain exactly product variant attributes."
        }
        require(variants.none { it.signature == signature }) {
            "Variant already exists with same combination."
        }
        ...
    }
}
```

Architectural reading: variant consistency is enforced inside Product aggregate, not scattered in controllers.

`Variant` exists as an entity inside this aggregate:

```kotlin
class Variant internal constructor(
    val id: VariantId,
    val code: String,
    val signature: String,
    var active: Boolean,
    var barcode: String?,
    private val options: MutableSet<VariantOption>,
    val createdAt: Instant
)
```

Architectural reading: `Variant` has identity (`VariantId`) and mutable lifecycle fields, so it is an entity, not a value object.

Attribute value representation in catalog is currently option-code based:

```kotlin
data class VariantOption(
    val attributeCode: String,
    val attributeValueCode: String
)
```

And persisted as:

```kotlin
@Entity
@Table(name = "sku_options")
class VariantOptionJpaEntity(
    ...,
    @Column(name = "attribute_code", nullable = false, length = 64)
    var attributeCode: String,
    @Column(name = "attribute_value_code", nullable = false, length = 64)
    var attributeValueCode: String
)
```

Architectural reading: catalog stores resolved option codes, while attribute semantics come from Attribute context APIs.

## 11. Aggregates in the PIM Model
Based on current code, practical aggregate picture is:

- **Attribute context**:
  - Aggregate: `AttributeSet`
  - Internal members: `AttributeGroup`, `AttributeSetItem`
  - Related entity model: `Attribute`
- **Taxonomy context**:
  - Aggregate: `Category`
  - Mapping entity: `CategoryAttributeSet` (attached at service layer)
- **Catalog context**:
  - Aggregate: `Product`
  - Internal entity: `Variant`
  - Value object: `VariantOption`

Entity vs Value Object evidence in repo:

- Entities with identity wrappers: `ProductId`, `VariantId`, `CategoryId`, `AttributeSetId`.
- Value-like types without identity: `VariantOption`.

This makes the ubiquitous language concrete instead of diagram-only.

## 12. How Services Communicate
Current runtime flow is synchronous REST between bounded contexts:

```text
Sources --> [Attribute | Taxonomy | Catalog] --> Channels
```

Concrete examples in code:

- Catalog -> Taxonomy: `/api/taxonomy/categories/{categoryCode}/metadata`
- Catalog -> Attribute: `/api/attribute/attributes/{code}` and `/api/attribute/attribute-sets/{setCode}`
- Taxonomy -> Attribute: `/api/attribute/attribute-sets/{setCode}`

Why this matters for DDD:

- Catalog asks Taxonomy for category metadata (`effectiveAttributeSetCodes`), then asks Attribute for definitions.
- No service reaches into another service DB.
- Domain ownership stays local; integration is explicit.

Also, what is **not** fully implemented yet:

- README describes snapshot-based `ATTRIBUTE_VALUE` modeling with typed value columns.
- In current source, Catalog variant values are represented by `VariantOption(attributeCode, attributeValueCode)`.
- So richer attribute-value lifecycle exists as a planned direction, not current aggregate code.

## 13. Conclusion
In this codebase, DDD did not start with whiteboard theory. It started with invariant placement.

- Attribute owns definition and set composition.
- Taxonomy owns hierarchy and effective set resolution.
- Catalog owns product/variant consistency.

Microservice boundaries became clearer once these domain rules were isolated inside each model and service. That is the main lesson: boundaries are easier to defend when each context has rules you can point to in code.

## 14. Teaser for Part 4
In Part 4, I’ll move from domain decisions to implementation mechanics: how these bounded contexts are exposed as Kotlin + Spring Boot microservices (module setup, adapters, controllers, and integration wiring).

---

# SHORTER_VERSION

## Title
**Building a PIM System (Part 3): Using DDD to Define Microservice Boundaries**

Part 2 focused on Clean Architecture inside each service. Part 3 is about boundary decisions between services.

In this repository, boundaries are visible in domain behavior:

- `attribute`: defines `Attribute`, `AttributeSet`, constraints, scopes/types.
- `taxonomy`: owns `Category` hierarchy and `effectiveAttributeSetCodes`.
- `catalog`: owns `Product` + `Variant` combination rules.

Domain overview:

```text
PIM Domain
|-- Attributes
|-- Taxonomy
`-- Catalog
```

Bounded context relation:

```text
[Attribute] ---> [Catalog] <--- [Taxonomy]
     ^                           |
     +---------------------------+
```

Service boundary evidence in code:

- Separate modules and DBs (`nexus_attribute`, `nexus_taxonomy`, `nexus_catalog`).
- REST clients across contexts (`AttributeServiceClient`, `TaxonomyServiceClient`).
- No direct cross-service DB access.

Aggregate examples:

- `AttributeSet` controls `addGroup` and `attachAttribute`.
- `Product` controls `setVariantAttributes` and `addVariant`.
- `Variant` is an entity under Product.
- `VariantOption(attributeCode, attributeValueCode)` is a value object.

One practical detail: `catalog` migration `V2__integration_refactor.sql` moved from ID-based coupling to code-based references. That is a concrete anti-`database-driven microservice` step.

What exists vs planned:

- **Exists**: code-based variant options and taxonomy-driven effective attribute set checks.
- **Planned/README-level**: snapshot-style `ATTRIBUTE_VALUE` typed-value model is not yet implemented in current catalog domain code.

Takeaway: DDD boundaries are easiest to defend when invariants are local and explicit in code, not implied by shared tables.

---

# TITLE_ALTERNATIVES

1. **PIM from Scratch (Part 3): DDD Boundaries We Actually Used in Code**
2. **Defining PIM Microservices with DDD: Real Lessons from Attribute, Taxonomy, and Catalog**
3. **From Domain Rules to Service Boundaries: DDD in a Kotlin PIM Backend**

---

# SUBTITLE_ALTERNATIVES

1. **How aggregate rules in our repository shaped microservice ownership**
2. **A code-first look at bounded contexts in Attribute, Taxonomy, and Catalog**
3. **Practical DDD decisions behind a real Kotlin + Spring Boot PIM implementation**

---

# LINKEDIN_POST

Part 3 of my “Building a PIM System” series is out.

This one is not a generic DDD write-up. It is based on real code from our Kotlin + Spring Boot repository.

I break down how bounded contexts emerged in practice:
- Attribute context: definitions + attribute sets
- Taxonomy context: category tree + effective set resolution
- Catalog context: product/variant invariants

I also show where we corrected boundary mistakes (ID-based coupling -> code-based integration) and what is still planned vs implemented.

If you are designing microservice boundaries in commerce systems, this is the part where DDD becomes concrete.

#ddd #microservices #kotlin #springboot #backend #architecture #pim #ecommerce

---

# COVER_IMAGE_PROMPT

Minimal tech blog cover illustration, white background, blue and gray palette, clean vector style, three bounded context blocks labeled Attribute, Taxonomy, Catalog, thin arrows from Attribute and Taxonomy into Catalog, small icons for tag, category tree, and SKU box, subtle code/diagram aesthetic, lots of whitespace, modern editorial Medium style, no people, no heavy text, 16:9
