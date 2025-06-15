create  table if not exists invites
(
    id          uuid         primary key,
    login       varchar(100) not null,
    key         varchar(6)   not null,
    deadline    TIMESTAMP    not null,
    person_id   numeric      not null
);

create table if not exists invites_roles
(
    invite_id uuid not null,
    role_id bigint not null,

    constraint fk_invites_roles_invite_id_on_invites foreign key (invite_id) references invites(id),
    constraint fk_invites_roles_role_id_on_roles foreign key (role_id) references roles(id)
);
