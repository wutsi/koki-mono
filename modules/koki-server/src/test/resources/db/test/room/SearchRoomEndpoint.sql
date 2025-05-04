INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA'),
           (200, null, 2, 'Ontario',  'Ontario', 'CA'),
           (2001, 200, 3, 'Toronto',  'Toronto', 'CA');

INSERT INTO T_ROOM(id, tenant_fk, type, status, city_fk, state_fk, country, title, deleted, max_guests)
    VALUES (111, 1, 1, 2, 1001, 100, 'CA', 'Room A', false, 2),
           (112, 1, 1, 1, 1001, 100, 'CA', 'Room A', false, 2),
           (113, 1, 1, 2, 1001, 100, 'CA', 'Room A', false, 4),
           (114, 1, 2, 3, 1001, 100, 'CA', 'Room A', false, 5),
           (115, 1, 1, 3, 2001, 100, 'CA', 'Room A', false, 6),
           (116, 1, 2, 3, 2001, 100, 'CA', 'Room A', false, 1),
           (199, 1, 1, 2, 2001, 100, 'CA', 'Room A', true, 10),
           (200, 2, 1, 1, 1001, 100, 'CA', 'Room A', false, 20);
