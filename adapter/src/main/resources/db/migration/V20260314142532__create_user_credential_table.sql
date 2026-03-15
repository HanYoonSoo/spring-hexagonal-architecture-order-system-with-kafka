create table user_credential (
    id bigserial primary key,
    user_id uuid not null,
    login_id varchar(255) not null,
    value varchar(255) not null,
    provider varchar(50) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    deleted_at timestamp,
    constraint fk_user_credential_user_id foreign key (user_id) references users (id)
);

create unique index uq_user_credential_login_id_active
on user_credential (login_id)
where deleted_at is null;
