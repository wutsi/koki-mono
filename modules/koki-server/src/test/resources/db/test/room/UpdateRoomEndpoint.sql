INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA'),
           (200, null, 2, 'Ontario',  'Ontario', 'CA'),
           (2001, 200, 3, 'Toronto',  'Toronto', 'CA');

INSERT INTO T_ROOM(id, tenant_fk, type, city_fk, state_fk, country, title, description)
    VALUES (111, 1, 1, 1001, 100, 'CA', 'Room A', 'Love it');
