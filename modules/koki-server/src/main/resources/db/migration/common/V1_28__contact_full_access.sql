INSERT INTO T_PERMISSION(id, module_fk, name, description) VALUES
    (2502, 250, 'room:delete', 'Delete rooms'),
    (2503, 250, 'room:full_access', 'Access to all rooms');

-- Grant 'room:full_access' to admin
DELETE FROM T_ROLE_PERMISSION where role_fk=1 AND permission_fk IN (SELECT id from T_PERMISSION where module_fk in (250, 251, 252));
INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk) VALUES(1, 2503);

-- Grant 'account:full_access' to admin
DELETE FROM T_ROLE_PERMISSION where role_fk=1 AND permission_fk IN (SELECT id from T_PERMISSION where module_fk=110);
INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk) VALUES(1, 1104);
