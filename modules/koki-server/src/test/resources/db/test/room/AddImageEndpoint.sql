INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA');

INSERT INTO T_FILE(id, tenant_fk, created_by_fk, name, content_type, content_length, url, deleted)
    VALUES (11, 1, 11,   'foo.png', 'image/png',  1000, 'https://www.file.com/foo.pdf', false),
           (22, 1, null, 'bar.png', 'image/png',  1000, 'https://www.file.com/bar.pdf', false),
           (33, 1, null, 'foo.png', 'image/png',  1000, 'https://www.file.com/foo.pdf', false),
           (44, 1, null, 'foo.pdf', 'image/png',  1000, 'https://www.file.com/foo.pdf', false),
           (55, 1, null, 'foo.pdf', 'application/pdf',  1000, 'https://www.file.com/foo.pdf', false),
           (99, 1, null, 'foo.jph', 'image/jpeg', 1000, 'https://www.file.com/foo.pdf', true);

INSERT INTO T_ROOM(id, tenant_fk, type, city_fk, state_fk, country, title)
    VALUES (111, 1, 1, 1001, 100, 'CA', 'Room A'),
           (112, 1, 1, 1001, 100, 'CA', 'Room B');

INSERT INTO T_ROOM_IMAGE(room_fk, file_fk)
    VALUES (111, 11),
           (111, 22),

           (112, 11),
           (112, 22);
