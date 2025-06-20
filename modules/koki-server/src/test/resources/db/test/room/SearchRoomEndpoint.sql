INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA'),
           (200, null, 2, 'Ontario',  'Ontario', 'CA'),
           (2001, 200, 3, 'Toronto',  'Toronto', 'CA');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, deleted, managed_by_fk)
    VALUES (22, 2, 'Inc', 'info@inc22.com', false, 1),
           (31, 1, 'Inc', 'info@inc31.com', false, 11),
           (32, 1, 'Inc', 'info@inc32.com', false, 1),
           (33, 1, 'Inc', 'info@inc33.com', false, 1);


INSERT INTO T_ROOM(id, tenant_fk, account_fk, type, status, neighborhood_fk, city_fk, state_fk, country, title, deleted, max_guests, number_of_rooms, number_of_bathrooms, category_fk)
    VALUES (111, 1, 33, 1, 2, 100111, 1001, 100, 'CA', 'Room A', false, 2, 2, 1, 33),
           (112, 1, 31, 1, 1, 100112, 1001, 100, 'CA', 'Room A', false, 2, 2, 1, null),
           (113, 1, 33, 1, 2, 100111, 1001, 100, 'CA', 'Room A', false, 4, 3, 2, null),
           (114, 1, 31, 2, 3, 100112, 1001, 100, 'CA', 'Room A', false, 5, 2, 1, null),
           (115, 1, 33, 1, 3, 200111, 2001, 100, 'CA', 'Room A', false, 6, 5, 5, 55),
           (116, 1, 32, 2, 3, 200112, 2001, 100, 'CA', 'Room A', false, 1, 6, 6, 55),
           (199, 1, 33, 1, 2, 200111, 2001, 100, 'CA', 'Room A', true, 10, 2, 1, null),
           (200, 2, 22, 1, 1, 200112, 1001, 100, 'CA', 'Room A', false, 20, 2, 1, null);

INSERT INTO T_ROOM_AMENITY(room_fk, amenity_fk)
    VALUES (111, 1),
           (111, 2),
           (114, 1),
           (114, 2),
           (114, 3),
           (115, 2),
           (116, 3),
           (116, 4);
