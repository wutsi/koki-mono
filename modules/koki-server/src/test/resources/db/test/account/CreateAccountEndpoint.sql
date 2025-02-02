INSERT INTO T_ATTRIBUTE(id, tenant_fk, name, label, description, choices, type, active)
    VALUES (100, 1, 'neq', 'NEQ', null, null, 1, true),
           (101, 1, 'tps', 'TPS', null, null, 1, true),
           (102, 1, 'tvq', 'TVQ', null, null, 1, false);

INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title, active)
    VALUES (100, 1, 1, 'T1', 'Tier 1', true),
           (101, 1, 1, 'T2', 'Tier 2', true),
           (102, 1, 1, 'T4', null, true);

