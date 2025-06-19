create sequence if not exists hibernate_sequence start with 1 increment by 1;

create table if not exists roles (
    id bigserial primary key,
    name varchar(50) not null unique
    );

create table if not exists clients (
    id uuid primary key,
    login varchar(100) not null unique,
    password varchar(100) not null,
    is_enabled boolean not null
    );

create table if not exists client_roles (
    client_id uuid not null,
    role_id bigint not null,

    constraint fk_client_roles_client foreign key (client_id) references clients(id),
    constraint fk_client_roles_role foreign key (role_id) references roles(id),

    primary key (client_id, role_id)
    );