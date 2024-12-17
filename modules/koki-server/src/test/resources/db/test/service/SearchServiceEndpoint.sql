
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_SERVICE(id, tenant_fk, name, title, description, base_url, authentication_type, username, password, api_key, deleted, active)
    VALUES (100, 1, 'SRV-100', 'Service #100', 'Description of service', 'https://localhost:7555', 2, 'admin', 'secret', 'api-key-00000', false, true),
           (110, 1, 'SRV-110', 'Sample script', null, 'https://www.google.com', 1, null, null, null, false, true),
           (120, 1, 'SRV-120', 'Sample script', null, 'https://www.google.com', 1, null, null, null, false, true),
           (130, 1, 'SRV-130', 'Sample script', null, 'https://www.google.com', 1, null, null, null, false, false),
           (199, 1, 'SRV-199', 'Sample script', null, 'https://www.google.com', 2, null, null, null, true, true),
           (200, 2, 'SRV-200', 'Sample script', null, 'https://www.google.com', 2, null, null, null, false, true);
