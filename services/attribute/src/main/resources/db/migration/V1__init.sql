create table if not exists attributes (
                                          id uuid primary key,
                                          code varchar(64) not null unique,
    data_type varchar(32) not null,
    scope varchar(32) not null,
    localizable boolean not null,
    channel_overridable boolean not null,
    multi_value boolean not null,
    created_at timestamptz not null
    );

create index if not exists idx_attributes_scope on attributes(scope);