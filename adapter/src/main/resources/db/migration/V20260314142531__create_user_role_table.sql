create table user_role (
    id bigserial primary key,
    user_id uuid not null,
    role varchar(50) not null,
    constraint fk_user_role_user_id foreign key (user_id) references users (id),
    constraint uq_user_role_user_id_role unique (user_id, role)
);
