INSERT INTO T_NOTE(id, tenant_fk, type, subject, body, summary, deleted, duration)
    VALUES (100, 1, 2, 'Yo', '<p>Man</p>', 'X', false, 15),
           (199, 1, 3, 'Yo', 'Man', 'Y', true, 11),
           (200, 2, 1, 'Yo', 'Man', 'Z', false, 22);
