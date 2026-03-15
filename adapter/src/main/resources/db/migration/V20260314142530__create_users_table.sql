create table users (
    id uuid primary key,
    name varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    deleted_at timestamp
);
