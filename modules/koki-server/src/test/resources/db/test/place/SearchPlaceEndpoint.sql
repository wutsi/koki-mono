INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES  (1, null, 3, 'Montreal', 'Montreal', 'CA'),
           (111, 1, 4, 'Downtown', 'Downtown', 'CA'),
           (222, 1, 4, 'Westmount', 'Westmount', 'CA');

INSERT INTO T_PLACE(id, tenant_fk, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, city_fk, summary, created_at, modified_at, deleted)
    VALUES (100, 1, 11, 'Downtown Park', 'Parc Downtown', 3, 3, 111, 1, 'Beautiful park', NOW(), NOW(), false),
           (101, 1, 11, 'École Centrale', 'Ecole Centrale', 2, 3, 111, 1, 'Great school', NOW(), NOW(), false),
           (102, 1, 11, 'Westmount Park', 'Parc Westmount', 3, 3, 222, 1, 'Nice park', NOW(), NOW(), false),
           (103, 1, 11, 'École Internationale', 'Ecole Internationale', 2, 1, 111, 1, 'Draft school', NOW(), NOW(), false),
           (104, 1, 11, 'Old Town', 'Vieille Ville', 1, 3, 111, 1, 'Historic neighborhood', NOW(), NOW(), false),
           (105, 1, 22, 'Other city', 'Other city', 0, 1, 333, 2, 'Other city', NOW(), NOW(), false),
           (200, 2, 22, 'Other tenant', 'Other tenant', 0, 1, 333, 2, 'Other tenant', NOW(), NOW(), false),
           (300, 1, 11, 'Deleted Place', 'Deleted Place', 0, 1, 999, 3, 'Deleted', NOW(), NOW(), true);

