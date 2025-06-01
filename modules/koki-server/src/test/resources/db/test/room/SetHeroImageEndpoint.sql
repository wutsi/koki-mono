INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA'),
           (200, null, 2, 'Ontario',  'Ontario', 'CA'),
           (2001, 200, 3, 'Toronto',  'Toronto', 'CA');

INSERT INTO T_ROOM(id, tenant_fk, account_fk, type, city_fk, state_fk, country, title, description)
    VALUES (111, 1, 33, 1, 1001, 100, 'CA', 'Room A', 'Love it'),
           (112, 1, 33, 1, 1001, 100, 'CA', 'Room A', 'Love it');

INSERT INTO T_FILE(id, tenant_fk, type, owner_fk, owner_type, created_by_fk, name, content_type, content_length, url, deleted)
    VALUES (110, 1, 2, 111,  12, null, 'foo.png', 'image/png', 1000, 'https://www.file.com/foo.pdf', false),
           (111, 1, 2, 111,  02, null, 'bar.png', 'image/png', 1000, 'https://www.file.com/bar.pdf', false),
           (112, 1, 2, 112,  12, null, 'bar.png', 'image/png', 1000, 'https://www.file.com/bar.pdf', false),
           (113, 1, 1, 111,  12, null, 'foo.png', 'image/png', 1000, 'https://www.file.com/foo.pdf', false);
