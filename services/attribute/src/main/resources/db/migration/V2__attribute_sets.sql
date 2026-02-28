create table if not exists attribute_sets (
                                              id uuid primary key,
                                              code varchar(64) not null unique,
    name varchar(255) not null,
    created_at timestamptz not null
    );

create table if not exists attribute_groups (
                                                id uuid primary key,
                                                attribute_set_id uuid not null references attribute_sets(id) on delete cascade,
    code varchar(64) not null,
    name varchar(255) not null,
    sort_order int not null,
    created_at timestamptz not null,
    unique(attribute_set_id, code)
    );

create table if not exists attribute_set_items (
                                                   id uuid primary key,
                                                   attribute_set_id uuid not null references attribute_sets(id) on delete cascade,
    attribute_id uuid not null references attributes(id) on delete restrict,
    group_id uuid null references attribute_groups(id) on delete set null,
    required boolean not null,
    sort_order int not null,
    constraints_json jsonb null,
    created_at timestamptz not null,
    unique(attribute_set_id, attribute_id)
    );

create index if not exists idx_groups_set on attribute_groups(attribute_set_id);
create index if not exists idx_items_set on attribute_set_items(attribute_set_id);
create index if not exists idx_items_attr on attribute_set_items(attribute_id);
