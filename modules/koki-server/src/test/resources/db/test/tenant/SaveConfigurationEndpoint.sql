
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_CONFIGURATION(tenant_fk, name, value)
    VALUES (1, 'delete', 'a1'),
           (1, 'update', 'b1'),
           (1, 'batch-0', 'batch-140'),
           (1, 'batch-1', 'batch-141'),
           (1, 'batch-2', 'batch-142'),

           (2, 'other-tenant', 'aa1');
