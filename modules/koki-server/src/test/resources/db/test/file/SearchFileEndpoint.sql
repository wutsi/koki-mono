
INSERT INTO T_TENANT(id, name, domain_name, locale, currency)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'USD');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1);

INSERT INTO T_FILE(id, tenant_fk, created_by_fk, workflow_instance_id, name, content_type, content_length, url)
    VALUES (100, 1, 11,   'wi-100', 'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf'),
           (200, 2, null, null,     'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf');
