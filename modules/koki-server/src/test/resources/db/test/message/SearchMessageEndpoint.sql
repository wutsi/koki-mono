INSERT INTO T_MESSAGE(id, tenant_fk, name, subject, body, active, created_at, modified_at, deleted)
    VALUES (100, 1, 'm-100', 'Subject 100', 'Hello', true,  '2020-01-01', now(), false),
           (110, 1, 'm-110', 'Subject 110', 'Hello', true,  '2021-01-01', now(), false),
           (120, 1, 'm-120', 'Subject 120', 'Hello', false, '2022-01-02', now(), false),
           (130, 1, 'm-130', 'Subject 130', 'Hello', false, '2023-01-01', now(), false),
           (199, 1, 'm-199', 'Subject 130', 'Hello', false, '2023-01-01', now(), true),
           (200, 2, 'm-200', 'Subject 200', 'Hello', false, now(), now(), false);
