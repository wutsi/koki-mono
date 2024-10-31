
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_ATTRIBUTE(id, tenant_fk, name, label, description, choices, type, active)
    VALUES (110, 1, 'delete', 'label-a', 'description-a', 'P1\nP2', 1, true),
           (120, 1, 'update', null, null, null, 2, true),
           (130, 1, 'new', null, null, '', 3, false),
           (140, 1, 'batch-0', null, null, '', 3, false),
           (141, 1, 'batch-1', null, null, '', 3, false),
           (142, 1, 'batch-2', null, null, '', 3, false),
           (143, 1, 'batch-3', null, null, '', 3, false),

           (200, 2, 'other-tenant', null, null, '', 3, false);

INSERT INTO T_CONFIGURATION(attribute_fk, value)
    VALUES (110, 'a1'),
           (120, 'b1'),
           (140, 'batch-140'),
           (141, 'batch-141'),
           (142, 'batch-142'),

           (200, 'aa1');
