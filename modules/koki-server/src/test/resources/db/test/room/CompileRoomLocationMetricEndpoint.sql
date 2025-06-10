INSERT INTO T_LOCATION (id, parent_fk, country, name, ascii_name)
    VALUES (237,       null,  'CM', 'Cameroon', 'Cameroon'),
           (23701,     237,   'CM', 'Centre', 'Centre'),
           (23702,     237,   'CM', 'Ouest', 'Ouest'),
           (2370201,   23702, 'CM', 'Bafoussam', 'bafoussam'),
           (2370202,   23702, 'CM', 'Bafang', 'bafang'),
           (2370103,   23702, 'CM', 'Bafou', 'Bafou'),
           (2370104,   23702, 'CM', 'Bana', 'Bana'),
           (2370101,   23701, 'CM', 'Yaounde', 'yaounde'),
           (2370102,   23701, 'CM', 'Obala', 'obala'),
           (237010100, 23701, 'CM', 'Bastos', 'bastos'),
           (237010101, 23701, 'CM', 'Essos', 'essos'),
           (237010102, 23701, 'CM', 'Mokolo', 'Mokolo'),
           (237010200, 23702, 'CM', 'Bepanda', 'Bepanda'),
           (237010201, 23702, 'CM', 'Bonandjo', 'Bonandjo'),
           (1,         null,  'CA', 'Canada', 'canada'),
           (1514,      1,     'CA', 'Quebec', 'quebec'),
           (151401,    1515,  'CA', 'Montreal', 'montreal')
;

INSERT INTO T_ROOM(id, tenant_fk, account_fk, type, status, city_fk, neighborhood_fk, country, title, deleted, max_guests, number_of_rooms, number_of_bathrooms, category_fk)
    VALUES (111, 1, 33, 1, 1, 2370101, 237010100, 'CM', 'Room A', false, 2, 2, 1, 33),
           (112, 1, 31, 1, 3, 2370101, 237010100, 'CM', 'Room A', false, 2, 2, 1, null),
           (113, 1, 33, 1, 3, 2370102, 237010200, 'CM', 'Room A', false, 4, 3, 2, null),
           (114, 1, 31, 2, 3, 2370102, 237010200, 'CM', 'Room A', false, 5, 2, 1, null),
           (115, 1, 33, 1, 3, 2370102, 237010200, 'CM', 'Room A', false, 6, 5, 5, 55),
           (116, 1, 32, 2, 3, 2370101, 237010101, 'CM', 'Room A', false, 1, 6, 6, 55),
           (199, 1, 33, 1, 3, 2370101, 237010101, 'CM', 'Room A', true, 10, 2, 1, null),
           (200, 2, 22, 1, 3, 2370101, 237010101, 'CM', 'Room A', false, 20, 2, 1, null);


INSERT INTO T_ROOM_LOCATION_METRIC(tenant_fk, location_fk, total_published_rentals)
    VALUES (1, 2370101, 100);
