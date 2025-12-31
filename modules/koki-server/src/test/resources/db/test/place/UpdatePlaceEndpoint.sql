INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country, latitude, longitude)
    VALUES (111, null, 3, 'Montréal', 'Montreal', 'CA', null, null),
           (222, 111, 4, 'Côte-des-Neiges', 'Cote-des-Neiges', 'CA', 45.4972159, -73.6390246);

INSERT INTO T_PLACE(id, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, city_fk, summary, created_at, modified_at, deleted, rating)
    VALUES (100, 11, 'Côte-des-Neiges', 'cote-des-neiges', 1, 1, 222, 111, 'A beautiful park', NOW(), NOW(), false, 4.5);

INSERT INTO T_PLACE_RATING(id, place_fk, criteria, value, reason)
    VALUES (1000, 100, 1, 5, 'Very safe area');

