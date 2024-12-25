INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'http://localhost:8081'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-1.com');

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{}'),
           (110, 1, 'f-110', 'Form 110', true, '{}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');
