
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_ATTRIBUTE(id, tenant_fk, name, label, description, choices, type, active)
    VALUES (10, 1, 'a', 'label-a', 'description-a', 'P1\nP2', 1, true),
           (11, 1, 'b', null, null, null, 2, true),
           (12, 1, 'c', null, null, '', 3, false),

           (20, 2, 'aa', null, null, '', 3, false);

INSERT INTO T_CONFIGURATION(attribute_fk, value)
    VALUES (10, 'a1'),
           (11, 'b1'),
           (12, 'c1'),

           (20, 'aa1');
