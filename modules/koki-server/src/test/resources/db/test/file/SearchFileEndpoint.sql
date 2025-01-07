INSERT INTO T_FILE(id, tenant_fk, created_by_fk, workflow_instance_id, form_id, name, content_type, content_length, url, deleted)
    VALUES (100, 1, 11,   'wi-100', 'f-100', 'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf', false),
           (101, 1, null, 'wi-100', null,    'bar.pdf', 'application/pdf', 1000, 'https://www.file.com/bar.pdf', false),
           (102, 1, null,  null,    null,    'foo.png', 'image/png', 1000, 'https://www.file.com/foo.pdf', false),
           (103, 1, null,  null,    'f-100', 'foo.jph', 'image/jpeg', 1000, 'https://www.file.com/foo.pdf', false),
           (104, 1, null, 'wi-110', 'f-110', 'foo.htm', 'text/html', 1000, 'https://www.file.com/foo.pdf', false),
           (199, 1, null, null,     null,    'foo.htm', 'text/html', 1000, 'https://www.file.com/foo.pdf', true),
           (200, 2, null, null,     null,    'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf', false);

INSERT INTO T_FILE_OWNER(file_fk, owner_fk, owner_type)
    VALUES (100, 11, 'ACCOUNT'),
           (104, 11, 'ACCOUNT'),
           (199, 11, 'ACCOUNT');
