create table if not exists product_contents (
    id uuid primary key,
    product_code varchar(64) not null unique,
    default_locale varchar(32) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table if not exists content_entries (
    id uuid primary key,
    product_content_id uuid not null references product_contents(id) on delete cascade,
    locale varchar(32) not null,
    channel varchar(64) null,
    title varchar(255) not null,
    short_description varchar(500) null,
    description text null,
    bullets_json jsonb not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    unique(product_content_id, locale, channel)
);

create index if not exists idx_product_contents_product on product_contents(product_code);
create index if not exists idx_content_entries_product_content on content_entries(product_content_id);
create index if not exists idx_content_entries_locale_channel on content_entries(locale, channel);
