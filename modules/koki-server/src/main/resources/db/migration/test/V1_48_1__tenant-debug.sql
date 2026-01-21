-- Assign tenant:debug permission to Administrator role
INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk) VALUES (1, 9011);
