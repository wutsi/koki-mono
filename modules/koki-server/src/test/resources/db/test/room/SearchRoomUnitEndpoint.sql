INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA'),
           (200, null, 2, 'Ontario',  'Ontario', 'CA'),
           (2001, 200, 3, 'Toronto',  'Toronto', 'CA');

INSERT INTO T_CATEGORY(id, parent_fk, type, level, name, long_name, active)
    VALUES (100, null, 1, 0, 'A',  'A', true);

INSERT INTO T_ROOM(id, tenant_fk, account_fk, type, status, city_fk, state_fk, country, title, description, number_of_bathrooms, number_of_beds, number_of_rooms, max_guests, postal_code, street, deleted, deleted_at, deleted_by_fk, price_per_night, currency)
    VALUES (111, 1, 33, 1, 2, 1001, 100, 'CA', 'Room A', 'This is the title of the room', 2, 4, 6, 10, '11111', '3030 Linton', false, null, null, 35, 'CAD'),
           (112, 1, 33, 1, 2, 1001, 100, 'CA', 'Room A', null, 1, 1, 1, 1, null, null, true, null, 3333, null, null),
           (200, 2, 33, 1, 2, 1001, 100, 'CA', 'Room A', null, 1, 1, 1, 1, null, null, true, null, 3333, null, null);

INSERT INTO T_ROOM_UNIT(id, tenant_fk, room_fk, floor, number, status, deleted)
    VALUES (1110, 1, 111, 1, '123', 2, false),
           (1111, 1, 111, 1, '124', 1, false),
           (1112, 1, 111, 1, '125', 2, false),
           (1113, 1, 111, 2, '211', 1, false),
           (1120, 1, 112, 2, '212', 2, false),
           (1121, 1, 112, 2, '333', 1, false),
           (1122, 1, 112, 2, '334', 1, true),
           (2000, 2, 200, 5, '555', 1, false);
