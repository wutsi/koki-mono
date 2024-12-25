INSERT INTO T_SCRIPT(id, tenant_fk, name, title, language, code, deleted, active)
    VALUES (100, 1, 'S-100', 'Create',        1, 'console.log(10+10)', false, true),
           (110, 1, 'S-110', 'Read',          1, 'console.log(10+10)', false, false),
           (120, 1, 'S-120', 'Update',        1, 'console.log(10+10)', false, true),
           (130, 1, 'S-130', 'Delete',        1, 'console.log(10+10)', false, true),
           (199, 1, 'S-199', 'Sample script', 1, 'console.log(10+10)', true,  true),
           (200, 2, 'S-200', 'Script #200',   2, 'print(10+10)',         false, false);
