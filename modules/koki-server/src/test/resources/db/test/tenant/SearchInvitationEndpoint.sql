INSERT INTO T_INVITATION(id, tenant_fk, type, status, display_name, email, deleted)
    VALUE ('100', 1, 1, 2, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('101', 1, 1, 2, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('102', 1, 1, 1, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('103', 1, 1, 3, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('199', 1, 0, 2, 'Deleted', 'deleted@gmail.com', true),
          ('200', 2, 0, 3, 'Deleted', 'deleted@gmail.com', false);
