INSERT INTO T_FILE(id, tenant_fk, type, status, created_by_fk, name, content_type, content_length, url, rejection_reason)
    VALUES (100, 1, 2, 3, 11,   'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf', 'Invalid file'),
           (200, 2, 1,  1, null, 'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf', null);
