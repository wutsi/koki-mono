INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (111, 333, 4, 'Downtown', 'Downtown', 'CA'),
           (222, 444, 4, 'Westmount', 'Westmount', 'CA');

INSERT INTO T_PLACE(id, tenant_fk, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, summary, created_at, modified_at, deleted)
    VALUES (100, 1, 11, 'Downtown Park', 'Parc Downtown', 3, 3, 111, 'Beautiful park', NOW(), NOW(), false),
           (101, 1, 11, 'École Centrale', 'Ecole Centrale', 2, 3, 111, 'Great school', NOW(), NOW(), false),
           (102, 1, 11, 'Westmount Park', 'Parc Westmount', 3, 3, 222, 'Nice park', NOW(), NOW(), false),
           (103, 1, 11, 'École Internationale', 'Ecole Internationale', 2, 1, 111, 'Draft school', NOW(), NOW(), false),
           (104, 1, 11, 'Old Town', 'Vieille Ville', 1, 3, 111, 'Historic neighborhood', NOW(), NOW(), false),
           (200, 2, 22, 'Other Place', 'Other Place', 0, 1, 333, 'Other tenant', NOW(), NOW(), false),
           (300, 1, 11, 'Deleted Place', 'Deleted Place', 0, 1, 333, 'Deleted', NOW(), NOW(), true);

