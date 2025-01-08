INSERT INTO T_NOTE(id, tenant_fk, subject, body, deleted)
    VALUES (100, 1, 'Yo', '<p>Man</p>', false),
           (101, 1, 'Yo', '<p>Man</p>', false),
           (102, 1, 'Yo', '<p>Man</p>', false),
           (103, 1, 'Yo', '<p>Man</p>', false),
           (104, 1, 'Yo', '<p>Man</p>', false),
           (199, 1, 'Yo', 'Man', true),
           (200, 2, 'Yo', 'Man', false);

INSERT INTO T_NOTE_OWNER(note_fk, owner_fk, owner_type)
    VALUES (100, 11, 'ACCOUNT'),
           (104, 11, 'ACCOUNT'),
           (199, 11, 'ACCOUNT');
