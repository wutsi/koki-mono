INSERT INTO T_FILE(id, tenant_fk, created_by_fk, name, content_type, content_length, url, deleted)
    VALUES (100, 1, 11,   'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf', false),
           (101, 1, null, 'bar.pdf', 'application/pdf', 1000, 'https://www.file.com/bar.pdf', false),
           (102, 1, null, 'foo.png', 'image/png', 1000, 'https://www.file.com/foo.pdf', false),
           (103, 1, null, 'foo.jph', 'image/jpeg', 1000, 'https://www.file.com/foo.pdf', false),
           (104, 1, null, 'foo.htm', 'text/html', 1000, 'https://www.file.com/foo.pdf', false),
           (199, 1, null, 'foo.htm', 'text/html', 1000, 'https://www.file.com/foo.pdf', true),
           (200, 2, null, 'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf', false);

INSERT INTO T_FILE_OWNER(file_fk, owner_fk, owner_type)
    VALUES (100, 11, 1),
           (104, 11, 1),
           (199, 11, 1);
