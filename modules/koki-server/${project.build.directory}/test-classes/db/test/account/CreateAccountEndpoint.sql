INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (111, 333, 3, 'Montreal', 'Montreal', 'CA'),
           (222, 777, 3, 'Quebec',   'Quebec', 'CA');

INSERT INTO T_ATTRIBUTE(id, tenant_fk, name, label, description, choices, type, active)
    VALUES (100, 1, 'neq', 'NEQ', null, null, 1, true),
           (101, 1, 'tps', 'TPS', null, null, 1, true),
           (102, 1, 'tvq', 'TVQ', null, null, 1, false);

INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title, active)
    VALUES (100, 1, 1, 'T1', 'Tier 1', true),
           (101, 1, 1, 'T2', 'Tier 2', true),
           (102, 1, 1, 'T4', null, true);

INSERT INTO T_ACCOUNT(tenant_fk, name, email, deleted)
    VALUES(1, 'Inc', 'info@inc1.com', false);

