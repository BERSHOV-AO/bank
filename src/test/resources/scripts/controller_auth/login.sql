INSERT INTO roles (id, name)
VALUES (1, 'CLIENT');

INSERT INTO clients (id, login, password, is_enabled)
VALUES ('ac9360fd-75ba-46c1-81dd-b9f54962aca5', 79090000001,
        '$2a$11$DSBQgX9TPdBqea/uPQ.DIeABAWw7aVvYPc4eWwzZEY4yAHzaVo77u', true);

INSERT INTO client_roles (client_id, role_id)
VALUES ('ac9360fd-75ba-46c1-81dd-b9f54962aca5', 1);