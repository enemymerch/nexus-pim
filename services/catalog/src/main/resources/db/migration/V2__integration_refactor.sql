alter table products add column if not exists category_code varchar(64);
update products set category_code = 'uncategorized' where category_code is null;
alter table products alter column category_code set not null;

-- Recreate variant attribute and sku option tables for code-based model.
drop table if exists sku_options;
drop table if exists product_variant_attributes;

create table if not exists product_variant_attributes (
    id uuid primary key,
    product_id uuid not null references products(id) on delete cascade,
    attribute_code varchar(64) not null,
    created_at timestamptz not null,
    unique(product_id, attribute_code)
);

create table if not exists sku_options (
    id uuid primary key,
    sku_id uuid not null references skus(id) on delete cascade,
    attribute_code varchar(64) not null,
    attribute_value_code varchar(64) not null,
    unique(sku_id, attribute_code)
);

create index if not exists idx_products_category_code on products(category_code);
create index if not exists idx_pva_product on product_variant_attributes(product_id);
create index if not exists idx_sku_options_sku on sku_options(sku_id);
