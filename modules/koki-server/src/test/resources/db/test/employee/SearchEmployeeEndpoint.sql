INSERT INTO T_EMPLOYEE(id, tenant_fk, first_name, last_name, status, deleted)
    VALUES (100, 1, 'Ray',    'Sponsible',  1, false),
           (110, 1, 'Raymond','Dube',       1, false),
           (120, 1, 'Frank',  'Dubois',     2, false),
           (130, 1, 'Simone', 'DeBeauvoir', 1, false),
           (199, 1, 'Yo',     'Deleted',    1, true),
           (200, 2, 'Roger',  'Milla',      0, false);
