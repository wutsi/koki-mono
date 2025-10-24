INSERT INTO T_INVITATION(id, tenant_fk, role_id, status, display_name, email, deleted)
    VALUE ('100', 1, 1111, 2, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('101', 1, 1111, 2, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('102', 1, 1111, 1, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('103', 1, 1111, 3, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('199', 1, null, 2, 'Deleted', 'deleted@gmail.com', true),
          ('200', 2, null, 3, 'Deleted', 'deleted@gmail.com', true);
