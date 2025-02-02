INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title, active)
    VALUES (100, 1, 1, 'T1', 'Tier 1',  true),
           (101, 1, 1, 'T2', 'Tier 2',  true),
           (102, 1, 2, 'P',  'Primary', true),
           (103, 1, 2, 'S',  'Spouse',  false),
           (200, 2, 1, 'aa', null,      true);
