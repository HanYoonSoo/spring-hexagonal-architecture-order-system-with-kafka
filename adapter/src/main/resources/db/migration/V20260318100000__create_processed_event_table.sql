create table processed_event (
    id uuid primary key,
    consumer_group_id varchar(100) not null,
    event_id uuid not null,
    event_type varchar(100) not null,
    processed_at timestamp not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create unique index uk_processed_event_group_event
    on processed_event (consumer_group_id, event_id);
