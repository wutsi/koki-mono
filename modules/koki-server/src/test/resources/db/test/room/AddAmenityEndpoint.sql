INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA');

INSERT INTO T_CATEGORY(id, parent_fk, type, level, name, long_name, active)
    VALUES (100, null, 1, 0, 'A',  'A', true);

INSERT INTO T_AMENITY(id, category_fk, name)
    VALUES (1, 100, 'A'),
           (2, 100, 'B'),
           (3, 100, 'C'),
           (4, 100, 'D'),
           (5, 100, 'E'),
           (100, 100, 'Z');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, deleted)
    VALUES(33, 1, 'Inc', 'info@inc1.com', false);

INSERT INTO T_ROOM(id, tenant_fk, account_fk, type, city_fk, state_fk, country, title)
    VALUES (111, 1, 33, 1, 1001, 100, 'CA', 'Room A'),
           (112, 1, 33, 1, 1001, 100, 'CA', 'Room B');


INSERT INTO T_ROOM_AMENITY(room_fk, amenity_fk)
    VALUES (111, 1),
           (111, 2),
           (111, 3),
           (111, 4),

           (112, 1),
           (112, 2);
