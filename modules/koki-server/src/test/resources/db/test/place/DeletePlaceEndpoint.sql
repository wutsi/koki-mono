INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (111, 333, 4, 'Downtown', 'Downtown', 'CA');

INSERT INTO T_PLACE(id, tenant_fk, created_by_fk, name, type, status, neighbourhood_fk, summary, created_at, modified_at)
    VALUES (100, 1, 11, 'Downtown Park', 2, 1, 111, 'A beautiful park', NOW(), NOW()),
           (200, 2, 22, 'Other Place', 0, 1, NULL, 'Other tenant', NOW(), NOW());

