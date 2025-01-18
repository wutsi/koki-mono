INSERT INTO T_USER(id, tenant_fk, email, password, display_name)
    VALUES (21, 1, 'ray.sponsible@gmail.com', '---', 'Ray');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'admin'),
           (20, 1, 'assigned-to-user'),
           (30, 1, 'role30');

INSERT INTO T_USER_ROLE(user_fk, role_fk)
    VALUES (21, 20);
