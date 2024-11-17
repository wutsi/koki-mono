
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_ROLE(id, tenant_fk, name, description, active)
    VALUES (10, 1, 'a', 'description-a',  true),
           (11, 1, 'b', null, true),
           (12, 1, 'c', null, false),

           (20, 2, 'aa', null, false),
           (22, 2, 'bb', null, false)
;
