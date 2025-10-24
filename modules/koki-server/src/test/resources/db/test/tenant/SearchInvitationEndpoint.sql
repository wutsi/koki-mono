INSERT INTO T_INVITATION(id, tenant_fk, role_id, status, display_name, email, deleted)
    VALUE ('100', 1, 1111, 2, 'Ray Sponsible', 'ray.sponsible@gmail.com', false),
          ('999', 1, null, 1, 'Deleted', 'deleted@gmail.com', true);
