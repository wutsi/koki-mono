INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec', 'CA'),
           (1001, 100, 3, 'Montreal', 'Montreal', 'CA'),
           (200, null, 2, 'Ontario',  'Ontario', 'CA'),
           (2001, 200, 3, 'Toronto',  'Toronto', 'CA');

INSERT INTO T_CATEGORY(id, parent_fk, type, level, name, long_name, active)
    VALUES (100, null, 1, 0, 'A',  'A', true);

INSERT INTO T_AMENITY(id, category_fk, name)
    VALUES (1, 100, 'A'),
           (2, 100, 'B');

INSERT INTO T_ROOM(id, tenant_fk, type, status, city_fk, state_fk, country, title, description, number_of_bathrooms, number_of_beds, number_of_rooms, max_guests, postal_code, street, deleted, deleted_at, deleted_by_fk, price_per_night, currency, checkin_time, checkout_time, neighborhood_fk)
    VALUES (111, 1, 1, 2, 1001, 100, 'CA', 'Room A', 'This is the title of the room', 2, 4, 6, 10, '11111', '3030 Linton', false, null, null, 35, 'CAD', '16:00', '12:00', 100111),
           (112, 1, 1, 2, 1001, 100, 'CA', 'Room A', null, 1, 1, 1, 1, null, null, true, null, 3333, null, null, null, null, null),
           (200, 2, 1, 2, 1001, 100, 'CA', 'Room A', null, 1, 1, 1, 1, null, null, true, null, 3333, null, null, null, null, null);

INSERT INTO T_ROOM_AMENITY(room_fk, amenity_fk)
    VALUES (111, 1),
           (111, 2);
