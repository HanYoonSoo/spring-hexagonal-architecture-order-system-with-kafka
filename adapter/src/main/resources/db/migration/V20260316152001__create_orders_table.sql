create table orders (
    id uuid primary key,
    user_id uuid not null,
    product_id bigint not null,
    quantity bigint not null,
    status varchar(30) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    deleted_at timestamp,
    constraint fk_orders_user_id foreign key (user_id) references users (id),
    constraint fk_orders_product_id foreign key (product_id) references product (id)
);

create index idx_orders_user_id on orders (user_id);
create index idx_orders_product_id on orders (product_id);
create index idx_orders_status on orders (status);
