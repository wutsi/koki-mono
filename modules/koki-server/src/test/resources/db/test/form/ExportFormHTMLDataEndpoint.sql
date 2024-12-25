INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'http://localhost:8081'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD', 'https://tenant-1.com');

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'admin'),
           (11, 1, 'writer'),
           (12, 1, 'reader'),
           (20, 2, 'accountant'),
           (21, 2, 'technician');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1),
           (22, 2, 'roger.milla@gmail.com', '---', 'Roger Milla', 1);

INSERT INTO T_USER_ROLE(user_fk, role_fk)
    VALUES (11, 20),
           (11, 21);

INSERT INTO T_FORM(id, tenant_fk, name, title, active, content)
    VALUES (100, 1, 'f-100', 'Form 100', true, '{"name":"FRM-001", "title":"Sample Form","description":"Description of the form"}'),
           (110, 1, 'f-110', 'Form 110', true, '{}'),
           (200, 2, 'f-200', 'Form 200', true, '{}');

INSERT INTO T_FORM_DATA(id, tenant_fk, form_fk, status, workflow_instance_id, data)
    VALUES (10011, 1, 100, 2, 'wi-100', '{"A":"aa","B":"bb"}'),
           (20022, 2, 200, 1, 'wi-200', '{"X":"xx"}');
