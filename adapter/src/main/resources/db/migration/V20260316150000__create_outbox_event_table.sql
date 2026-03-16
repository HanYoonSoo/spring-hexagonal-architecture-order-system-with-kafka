create table outbox_event (
    id uuid primary key,
    event_type varchar(100) not null,
    topic varchar(255) not null,
    event_key varchar(255),
    payload text not null,
    occurred_at timestamp with time zone not null,
    status varchar(20) not null,
    retry_count integer not null default 0,
    next_retry_at timestamp not null,
    published_at timestamp,
    last_error varchar(1000),
    created_at timestamp not null,
    updated_at timestamp not null
);

create index idx_outbox_event_pending
on outbox_event (status, next_retry_at, created_at);
