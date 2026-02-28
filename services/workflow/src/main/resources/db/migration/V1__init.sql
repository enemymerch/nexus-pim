create table if not exists workflow_instances (
    id uuid primary key,
    product_code varchar(64) not null unique,
    state varchar(16) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table if not exists workflow_transitions (
    id uuid primary key,
    workflow_instance_id uuid not null references workflow_instances(id) on delete cascade,
    from_state varchar(16) not null,
    action varchar(16) not null,
    to_state varchar(16) not null,
    actor varchar(128) not null,
    comment text null,
    happened_at timestamptz not null
);

create index if not exists idx_workflow_instances_product on workflow_instances(product_code);
create index if not exists idx_workflow_transitions_instance on workflow_transitions(workflow_instance_id);
create index if not exists idx_workflow_transitions_happened_at on workflow_transitions(happened_at);
