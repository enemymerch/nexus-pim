create table if not exists categories (
    id uuid primary key,
    code varchar(64) not null unique,
    name varchar(255) not null,
    parent_id uuid null references categories(id) on delete restrict,
    depth int not null,
    path varchar(1024) not null,
    created_at timestamptz not null,
    check (depth >= 0)
);

create index if not exists idx_categories_parent on categories(parent_id);
create index if not exists idx_categories_path on categories(path);

create table if not exists category_attribute_sets (
    id uuid primary key,
    category_id uuid not null references categories(id) on delete cascade,
    attribute_set_code varchar(64) not null,
    created_at timestamptz not null,
    unique(category_id, attribute_set_code)
);

create index if not exists idx_cat_attr_sets_category on category_attribute_sets(category_id);
create index if not exists idx_cat_attr_sets_attr_code on category_attribute_sets(attribute_set_code);
