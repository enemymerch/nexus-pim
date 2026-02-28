create table if not exists products (
    id uuid primary key,
    code varchar(64) not null unique,
    name varchar(255) not null,
    status varchar(16) not null,
    created_at timestamptz not null
);

create table if not exists product_variant_attributes (
    id uuid primary key,
    product_id uuid not null references products(id) on delete cascade,
    attribute_id uuid not null,
    created_at timestamptz not null,
    unique(product_id, attribute_id)
);

create table if not exists skus (
    id uuid primary key,
    product_id uuid not null references products(id) on delete cascade,
    code varchar(64) not null,
    signature varchar(1024) not null,
    active boolean not null,
    barcode varchar(128) null,
    created_at timestamptz not null,
    unique(product_id, code),
    unique(product_id, signature)
);

create table if not exists sku_options (
    id uuid primary key,
    sku_id uuid not null references skus(id) on delete cascade,
    attribute_id uuid not null,
    attribute_value_id uuid not null,
    unique(sku_id, attribute_id)
);

create index if not exists idx_pva_product on product_variant_attributes(product_id);
create index if not exists idx_skus_product on skus(product_id);
create index if not exists idx_skus_signature on skus(signature);
create index if not exists idx_sku_options_sku on sku_options(sku_id);
