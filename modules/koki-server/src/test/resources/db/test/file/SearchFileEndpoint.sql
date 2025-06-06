INSERT INTO T_FILE(id, tenant_fk, type, owner_fk, status, owner_type, created_by_fk, name, content_type, content_length, url, deleted)
    VALUES (100, 1, 1, 11,   1, 1,    null, 'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf', false),
           (101, 1, 1, null, 2, null, null, 'bar.pdf', 'application/pdf', 1000, 'https://www.file.com/bar.pdf', false),
           (102, 1, 2, null, 1, null, null, 'foo.png', 'image/png', 1000, 'https://www.file.com/foo.pdf', false),
           (103, 1, 2, null, 2, null, null, 'foo.jpg', 'image/jpeg', 1000, 'https://www.file.com/foo.pdf', false),
           (104, 1, 1, 11,   1, 1,    null, 'foo.htm', 'text/html', 1000, 'https://www.file.com/foo.pdf', false),
           (199, 1, 1, 11,   2, 1,    null, 'foo.htm', 'text/html', 1000, 'https://www.file.com/foo.pdf', true),
           (200, 2, 1, null, 1, null, null, 'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf', false);

INSERT INTO T_LABEL(id, tenant_fk, name, display_name)
    VALUES (1, 1, '2023', '2023'),
           (2, 1, 'T4', 'T4'),
           (3, 1, 'T5', 'T5');

INSERT INTO T_FILE_LABEL(file_fk, label_fk)
    VALUES (100, 1),
           (100, 3),
           (101, 2);
