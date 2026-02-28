create table if not exists search_documents (
    id uuid primary key,
    product_code varchar(64) not null unique,
    category_code varchar(64) not null,
    status varchar(32) not null,
    default_locale varchar(32) not null,
    content_title varchar(255) null,
    content_description text null,
    media_count int not null default 0,
    variant_count int not null default 0,
    searchable_text text not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index if not exists idx_search_documents_product on search_documents(product_code);
create index if not exists idx_search_documents_category on search_documents(category_code);
create index if not exists idx_search_documents_status on search_documents(status);
