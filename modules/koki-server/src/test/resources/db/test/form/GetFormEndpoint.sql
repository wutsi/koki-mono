INSERT INTO T_FORM(id, tenant_fk, name, title, description, active, deleted, content)
    VALUES (100, 1, 'f-100', 'Form 100', 'Love it', true, false, '{"title":"Sample Form","description":"Description of the form"}'),
           (199, 1, 'f-199', 'Form 199', null, true, true, '{}'),
           (200, 2, 'f-200', 'Form 200', null, true, true, '{}');
