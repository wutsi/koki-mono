INSERT INTO T_INVITATION(id, tenant_fk, role_id, status, display_name, email, deleted, expires_at)
    VALUE ('100', 1, 1111, 1, 'Ray Sponsible', 'ray.sponsible@gmail.com', false, '2020-01-01'),
          ('101', 1, 1111, 2, 'Ray Sponsible', 'ray.sponsible@gmail.com', false, '2020-01-01'),
          ('102', 1, 1111, 1, 'Ray Sponsible', 'ray.sponsible@gmail.com', false, '2020-01-01'),
          ('103', 1, 1111, 3, 'Ray Sponsible', 'ray.sponsible@gmail.com', false, '2020-01-01'),
          ('199', 1, null, 2, 'Deleted', 'deleted@gmail.com', true, '2020-01-01'),
          ('200', 2, null, 1, 'Another Tenant', 'deleted@gmail.com', false, '2020-01-01');
