INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com');

INSERT INTO T_INVITATION(id, tenant_fk, status, display_name, email, deleted, expires_at)
    VALUE ('100', 1, 1, 'Ray Sponsible', 'ray.sponsible@gmail.com', false, '2020-01-01'),
          ('101', 1, 2, 'Ray Sponsible', 'ray.sponsible@gmail.com', false, '2020-01-01'),
          ('102', 1, 1, 'Ray Sponsible', 'ray.sponsible@gmail.com', false, '2020-01-01'),
          ('103', 1, 3, 'Ray Sponsible', 'ray.sponsible@gmail.com', false, '2020-01-01'),
          ('199', 1, 2, 'Deleted', 'deleted@gmail.com', true, '2020-01-01'),
          ('200', 2, 1, 'Another Tenant', 'deleted@gmail.com', false, '2020-01-01');
