create table product (
    id bigserial primary key,
    name varchar(255) not null,
    description varchar(255),
    price bigint not null,
    stock bigint not null default 0,
    created_at timestamp not null,
    updated_at timestamp not null
);
