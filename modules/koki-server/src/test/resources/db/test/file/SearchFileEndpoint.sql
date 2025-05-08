INSERT INTO T_FILE(id, tenant_fk, file_type, owner_fk, owner_type, created_by_fk, name, content_type, content_length, url, deleted)
    VALUES (100, 1, 3,  11,    1,    null, 'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf', false),
           (101, 1, 3,  null,  null, null, 'bar.pdf', 'application/pdf', 1000, 'https://www.file.com/bar.pdf', false),
           (102, 1, 13, null,  null, null, 'foo.png', 'image/png', 1000, 'https://www.file.com/foo.pdf', false),
           (103, 1, 13, null,  null, null, 'foo.jpg', 'image/jpeg', 1000, 'https://www.file.com/foo.pdf', false),
           (104, 1, 3,  11,    1,    null, 'foo.htm', 'text/html', 1000, 'https://www.file.com/foo.pdf', false),
           (199, 1, 3,  11,    1,    null, 'foo.htm', 'text/html', 1000, 'https://www.file.com/foo.pdf', true),
           (200, 2, 3,  null,  null, null, 'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf', false);

INSERT INTO T_LABEL(id, tenant_fk, name, display_name)
    VALUES (1, 1, '2023', '2023'),
           (2, 1, 'T4', 'T4'),
           (3, 1, 'T5', 'T5');

INSERT INTO T_FILE_LABEL(file_fk, label_fk)
    VALUES (100, 1),
           (100, 3),
           (101, 2);
