INSERT INTO T_SCRIPT(id, tenant_fk, name, title, language, code, deleted)
    VALUES (100, 1, 'S-100', 'Sample script', 1, 'console.log(10+10)', false),
           (110, 1, 'S-110', 'Sample script', 1, 'console.log(10+10)', false),
           (120, 1, 'S-120', 'Sample script', 1, 'console.log(10+10)', false),
           (199, 1, 'S-199', 'Sample script', 1, 'console.log(10+10)', true),
           (200, 2, 'S-200', 'Script #200', 2, 'print(10+10)', false);
