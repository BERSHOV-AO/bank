create sequence if not exists hibernate_sequence start with 1 increment by 1;

-- Создание таблицы ролей
create table if not exists roles (
    id bigserial primary key,
    name varchar(50) not null unique
    );

-- Создание таблицы пользователей MIS
create table if not exists mis_users (
    id uuid primary key,
    login varchar(100) not null unique,
    password varchar(100) not null,
    is_enabled  boolean      not null
    );

-- Создание связующей таблицы ролей и пользователей
create table if not exists mis_users_roles (
    user_id uuid not null,
    role_id bigint not null,

    constraint fk_mis_users_roles_user foreign key (user_id) references mis_users(id),
    constraint fk_mis_users_roles_role foreign key (role_id) references roles(id),

    primary key (user_id, role_id)
    );