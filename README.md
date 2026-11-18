# Nexus PIM

## Local workspace UI

The React + TypeScript + Vite frontend is in [`frontend/`](frontend/README.md). It connects to all seven
backend services and exposes their existing operations across products, categories, attributes,
attribute sets, content, media, workflow and search. No mock dataset is used by the running UI.

With Java 21 and Docker available, start infrastructure using `docker compose up -d`, then run the
seven Spring Boot services on their configured ports 8081–8087. The database initializer creates all
seven databases on a fresh PostgreSQL volume; existing volumes are preserved and need any missing
databases provisioned separately. The supplied credentials and disabled Elasticsearch security are
local-development defaults, not production settings.

```powershell
cd frontend
# Node.js >=22.12
npm ci
npm run dev
```

Open **http://127.0.0.1:5173**. The development proxy routes API requests to the local backend services.
Run `npm run verify` for TypeScript checks, production build and frontend tests. See the frontend README
for production proxy requirements and current limitations, including authentication and binary storage.

> Nexus PIM is a distributed, event-driven Product Information
> Management platform built with Kotlin and Spring Boot.

Licensed under the **Apache License 2.0**.

------------------------------------------------------------------------

# 1. What is PIM?

A **Product Information Management (PIM)** system centralizes and
manages all product-related information across an organization.

Unlike ERP systems that manage transactional data (orders, stock,
finance), PIM focuses on:

-   Product metadata
-   Dynamic attributes
-   Localization
-   Media assets
-   Category classification
-   Channel-based overrides
-   Versioning & publishing lifecycle

Nexus PIM acts as the **Single Source of Truth** for product information
across multiple channels such as:

-   E-commerce platforms
-   Mobile applications
-   Marketplaces
-   Internal systems

------------------------------------------------------------------------

# 2. Sub-Domains

The system follows Domain-Driven Design (DDD) principles.

## Core Domains

### Catalog

-   Product (Product Family)
-   Variant (SKU)
-   Variant Axis (e.g., color, size)

### Attribute Management

-   Attribute
-   Attribute Set
-   Attribute Group
-   Category-based validation rules
-   Dynamic value types

### Taxonomy

-   Category hierarchy
-   Category-to-attribute-set mapping

### Workflow & Versioning

-   Snapshot-based version control
-   Draft / Approved / Published lifecycle
-   Active snapshot model

------------------------------------------------------------------------

## Supporting Domains

### Content

-   Multi-language fields (name, description, SEO)
-   Locale fallback strategy

### Media

-   Asset metadata
-   Product-media relationships

### Search

-   Elasticsearch projection
-   Channel-aware resolved documents
-   High-performance filtering & faceting

------------------------------------------------------------------------

# 3. ERD (High-Level)

Below is a simplified high-level relational model.

    CATEGORY
     |- id (PK)
     |- parent_id (FK -> CATEGORY.id)
     +- code

    ATTRIBUTE_SET
     |- id (PK)
     +- code

    ATTRIBUTE
     |- id (PK)
     |- code
     |- data_type
     |- scope (PRODUCT | VARIANT)
     |- is_localizable
     +- is_channel_overridable

    ATTRIBUTE_SET_ATTRIBUTE
     |- attribute_set_id (FK)
     |- attribute_id (FK)
     |- is_required
     +- constraints_json

    PRODUCT
     |- id (PK)
     |- code
     |- category_id (FK)
     +- status

    PRODUCT_VARIANT
     |- id (PK)
     |- product_id (FK)
     |- sku
     +- status

    CATALOG_SNAPSHOT
     |- id (PK)
     |- version_no
     +- status

    ATTRIBUTE_VALUE
     |- id (PK)
     |- snapshot_id (FK)
     |- entity_type (PRODUCT | VARIANT)
     |- entity_id
     |- attribute_id (FK)
     |- channel_id (nullable)
     |- locale (nullable)
     +- typed value columns

The system supports:

-   Dynamic attribute modeling
-   Channel-based override resolution
-   Snapshot-based versioning
-   Locale-aware values

------------------------------------------------------------------------

# 4. Tech Stack

## Backend

-   Kotlin
-   Spring Boot 3
-   Spring Data JPA
-   Flyway

## Database

-   PostgreSQL (Transactional Write Model)
-   Elasticsearch (Search & Read Model)
-   Redis (Caching Layer)

## Messaging

-   Kafka (Event Backbone)

## Observability

-   Micrometer
-   Prometheus
-   Grafana
-   OpenTelemetry

------------------------------------------------------------------------

# 5. Infrastructure

The platform is designed to run in containerized environments.

## Local Development

-   Docker Compose
-   PostgreSQL
-   Kafka
-   Elasticsearch
-   Redis

## Production (Planned)

-   Kubernetes
-   Horizontal scaling
-   Distributed logging & tracing
-   Centralized monitoring

Architectural principles:

-   Database per service
-   No shared database access
-   Event-driven synchronization
-   Eventual consistency
-   Idempotent consumers

------------------------------------------------------------------------

# 6. Microservices

Nexus PIM is structured as a microservices architecture.

Service     Responsibility
  ----------- --------------------------------------------
catalog     Product & Variant management
attribute   Dynamic attribute definitions & validation
taxonomy    Category hierarchy
content     Localization
media       Media metadata
workflow    Versioning & publishing lifecycle
search      Elasticsearch projection & search API

Each microservice:

-   Owns its database
-   Exposes REST APIs
-   Publishes domain events
-   Communicates asynchronously via Kafka

------------------------------------------------------------------------

# License

This project is licensed under the **Apache License 2.0**.

See the `LICENSE` file for details.
