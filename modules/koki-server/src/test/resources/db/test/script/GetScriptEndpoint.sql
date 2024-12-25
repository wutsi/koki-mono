INSERT INTO T_SCRIPT(id, tenant_fk, name, title, description, language, code, parameters, deleted)
    VALUES (100, 1, 'S-100', 'Sample script', 'description 100', 1, 'console.log(a+b)',  'a,b', false),
           (199, 1, 'S-199', 'Sample script', null,              1, 'console.log(10+10)', null, true),
           (200, 2, 'S-200', 'Script #200',   null,              2, 'print(10+10)',       null, false);
