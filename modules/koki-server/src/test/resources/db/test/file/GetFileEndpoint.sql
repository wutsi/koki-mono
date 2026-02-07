INSERT INTO T_FILE(id, tenant_fk, type, owner_fk, owner_type, status, created_by_fk, name, content_type, content_length, url, rejection_reason)
    VALUES (100, 1, 1, null, null,3, 11,   'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf', 'Invalid file'),
           (101, 1, 2, 111, 1,3, 11,   'foo.png', 'image/png', 1000, 'https://www.file.com/foo.png', 'Invalid file'),
           (200, 2, 1,  null, null,1, null, 'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf', null);
