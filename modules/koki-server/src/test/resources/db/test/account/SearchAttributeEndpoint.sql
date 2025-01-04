INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-1.com');

INSERT INTO T_ATTRIBUTE(tenant_fk, name, label, description, choices, type, active)
    VALUES (1, 'a', 'label-a', 'description-a', 'P1\nP2', 1, true),
           (1, 'b', null, null, null, 2, true),
           (1, 'c', null, null, '', 3, false),

           (2, 'aa', null, null, '', 3, false),
           (2, 'bb', null, null, '', 3, false)
;
