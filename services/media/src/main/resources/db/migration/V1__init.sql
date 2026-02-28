create table if not exists media_assets (
    id uuid primary key,
    product_code varchar(64) not null,
    variant_code varchar(64) null,
    media_type varchar(16) not null,
    role varchar(32) not null,
    status varchar(16) not null,
    mime_type varchar(128) not null,
    original_filename varchar(255) not null,
    storage_bucket varchar(128) not null,
    storage_key varchar(512) not null unique,
    size_bytes bigint null,
    checksum_sha256 varchar(64) null,
    locale varchar(32) null,
    channel varchar(64) null,
    sort_order int not null default 0,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    uploaded_at timestamptz null
);

create index if not exists idx_media_assets_product on media_assets(product_code);
create index if not exists idx_media_assets_variant on media_assets(variant_code);
create index if not exists idx_media_assets_status on media_assets(status);
create index if not exists idx_media_assets_locale_channel on media_assets(locale, channel);
