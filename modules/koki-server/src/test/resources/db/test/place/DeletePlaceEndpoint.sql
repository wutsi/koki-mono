INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (111, 333, 4, 'Downtown', 'Downtown', 'CA');

INSERT INTO T_PLACE(id, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, city_fk, summary, created_at, modified_at)
    VALUES (100, 11, 'Downtown Park', 'Downtown Park', 2, 1, 111, 1,'A beautiful park', NOW(), NOW());

