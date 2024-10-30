
INSERT INTO T_USER(id, email, password, display_name) VALUE(11, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible');

INSERT INTO T_TENANT(id, owner_fk, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 11, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 11, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');


INSERT INTO T_ATTRIBUTE(tenant_fk, name, label, description, choices, type, active)
    VALUES (1, 'a', 'label-a', 'description-a', 'P1\nP2', 1, true),
           (1, 'b', null, null, null, 2, true),
           (1, 'c', null, null, '', 3, false),

           (2, 'aa', null, null, '', 3, false),
           (2, 'bb', null, null, '', 3, false)
;
