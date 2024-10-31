
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_ROLE(tenant_fk, name, description, active)
    VALUES (1, 'a', 'description-a',  true),
           (1, 'b', null, true),
           (1, 'c', null, false),

           (2, 'aa', null, false),
           (2, 'bb', null, false)
;
