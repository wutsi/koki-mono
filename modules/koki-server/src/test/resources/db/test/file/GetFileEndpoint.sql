INSERT INTO T_FILE(id, tenant_fk, created_by_fk, workflow_instance_id, form_id, name, content_type, content_length, url)
    VALUES (100, 1, 11,   'wi-100', 'f-100', 'foo.pdf', 'application/pdf', 1000, 'https://www.file.com/foo.pdf'),
           (200, 2, null, null,     null,    'bar.pdf', 'application/pdf', 4000, 'https://www.file.com/bar.pdf');
