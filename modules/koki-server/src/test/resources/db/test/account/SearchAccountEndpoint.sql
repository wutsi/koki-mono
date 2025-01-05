INSERT INTO T_ACCOUNT(id, tenant_fk, name, phone, mobile, email, deleted, created_by_fk, modified_by_fk, managed_by_fk)
    VALUES(1000, 1, 'Inc', '+5147580000', '+5147580011', 'info@inc.com',           false, 11, null, null),
          (1001, 1, 'Pixar Inc', '+18007580000', '+18009310011', 'info@pixar.com', false, 11, 12, 13),
          (1002, 1, 'Apple Inc', '+18887580000', '+18889310011', 'hi@apple.com', false, null, 12, 13),
          (1003, 1, 'Expedia Group', null, null, 'hi@expediagroup.com',            false, null, null, 13),
          (1999, 1, 'Deleted', null, null, null,                                   true,  11, 12, 13),
          (2000, 2, 'Inc', null, null, null, false, null, null, null);
