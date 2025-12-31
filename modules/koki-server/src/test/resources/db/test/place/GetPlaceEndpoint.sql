INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (111, null, 2, 'Downtown', 'Downtown', 'CA'),
           (222, 111, 4, 'Downtown', 'Downtown', 'CA');

INSERT INTO T_PLACE(id, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, city_fk, summary, created_at, modified_at, deleted, rating)
    VALUES (100, 11, 'Downtown Park', 'Downtown Park', 2, 1, 222, 111,'A beautiful park', NOW(), NOW(), false, 4.5),
           (300, 22, 'Other Place', 'Other Place', 0, 1, 222, 111,'Other tenant', NOW(), NOW(), true, 3.3);

INSERT INTO T_PLACE_RATING(id, place_fk, criteria, value, reason)
    VALUES (1000, 100, 1, 5, 'Very safe area'),
           (1001, 100, 2, 4, 'Good amenities');

