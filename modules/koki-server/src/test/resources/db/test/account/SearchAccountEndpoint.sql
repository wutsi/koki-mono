INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title, active)
    VALUES (100, 1, 1, 'T1', 'Tier 1', true),
           (101, 1, 1, 'T2', 'Tier 2', true),
           (102, 1, 1, 'T4', null, true),
           (103, 1, 1, 'T5', null, false),
           (200, 2, 1, 'aa', null, false);

INSERT INTO T_ACCOUNT(id, tenant_fk, account_type_fk, name, phone, mobile, email, deleted, created_by_fk, modified_by_fk, managed_by_fk)
    VALUES(1000, 1, 100,  'Inc', '+5147580000', '+5147580011', 'info@inc.com',           false, 11, null, null),
          (1001, 1, 101,  'Pixar Inc', '+18007580000', '+18009310011', 'info@pixar.com', false, 11, 12, 13),
          (1002, 1, 100,  'Apple Inc', '+18887580000', '+18889310011', 'hi@apple.com',   false, null, 12, 13),
          (1003, 1, 101,  'Expedia Group', null, null, 'hi@expediagroup.com',            false, null, null, 13),
          (1999, 1, null, 'Deleted', null, null, 'info@inc2.com',                        true,  11, 12, 13),
          (2000, 2, null, 'Inc', null, null, 'info@inc1.com',                            false, null, null, null);
