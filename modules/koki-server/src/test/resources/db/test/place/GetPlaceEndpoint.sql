INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (111, 333, 4, 'Downtown', 'Downtown', 'CA');

INSERT INTO T_PLACE(id, tenant_fk, created_by_fk, name, type, status, neighbourhood_fk, summary, created_at, modified_at, deleted)
    VALUES (100, 1, 11, 'Downtown Park', 2, 1, 111, 'A beautiful park', NOW(), NOW(), false),
           (200, 2, 11, 'Central School', 1, 3, 111, 'Great school', NOW(), NOW(), false),
           (300, 1, 22, 'Other Place', 0, 1, NULL, 'Other tenant', NOW(), NOW(), true);

INSERT INTO T_PLACE_RATING(id, place_fk, criteria, value, reason)
    VALUES (1000, 100, 1, 5, 'Very safe area'),
           (1001, 100, 2, 4, 'Good amenities');

