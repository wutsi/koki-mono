INSERT INTO T_EMPLOYEE(id, tenant_fk, first_name, last_name, status, job_title, hourly_wage, currency, deleted)
    VALUES (100, 1, 'Ray',   'Sponsible', 1, 'Director of Tech', 10000, 'XAF', false),
           (199, 1, 'Yo',    'Deleted',   0, null, null, null, true),
           (200, 2, 'Roger', 'Milla',     0, null, null, null, false);
