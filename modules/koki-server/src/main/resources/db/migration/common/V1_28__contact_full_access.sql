INSERT INTO T_PERMISSION(id, module_fk, name, description) VALUES
    (1203, 120, 'room:full_access', 'Access to all contacts');

-- Grant 'room:full_access' to admin
DELETE FROM T_ROLE_PERMISSION where role_fk=1 AND permission_fk IN (SELECT id from T_PERMISSION where module_fk = 120);
INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk) VALUES(1, 1203);
