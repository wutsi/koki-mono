INSERT INTO T_FILE(id, tenant_fk, created_by_fk,  name, content_type, content_length, url, owner_fk, owner_type)
    VALUES (100, 1, 11,   'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf', 111, 1),
           (200, 2, null, 'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf', null, null);
