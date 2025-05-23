INSERT INTO T_FILE(id, tenant_fk, type, status, created_by_fk, name, content_type, content_length, url, rejection_reason)
    VALUES (100, 1, 2, 3, 11,   'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf', 'Invalid file'),
           (200, 2, 1,  1, null, 'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf', null);

INSERT INTO T_LABEL(id, tenant_fk, name, display_name)
    VALUES (1, 1, '2023', '2023'),
           (2, 1, 'T4', 'T4'),
           (3, 1, 'T5', 'T5');

INSERT INTO T_FILE_LABEL(file_fk, label_fk)
    VALUES (100, 1),
           (100, 3);
