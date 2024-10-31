
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_ATTRIBUTE(tenant_fk, name, label, description, choices, type, active)
    VALUES (1, 'a', 'label-a', 'description-a', 'P1\nP2', 1, true),
           (1, 'b', null, null, null, 2, true),
           (1, 'c', null, null, '', 3, false),

           (2, 'aa', null, null, '', 3, false),
           (2, 'bb', null, null, '', 3, false)
;
