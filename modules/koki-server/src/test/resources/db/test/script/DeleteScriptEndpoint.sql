
INSERT INTO T_TENANT(id, name, domain_name, locale, currency, created_at)
    VALUES
        (1, 'test1', 'test1.com', 'en_US', 'USD', '2020-01-22 12:30'),
        (2, 'test2', 'test2.com', 'en_US', 'USD', '2020-01-22 12:30');

INSERT INTO T_SCRIPT(id, tenant_fk, name, title, language, code, deleted)
    VALUES (100, 1, 'S-100', 'Sample script', 1, 'console.log(10+10)', false),
           (110, 1, 'S-110', 'Sample script', 1, 'console.log(10+10)', false),
           (199, 1, 'S-199', 'Sample script', 1, 'console.log(10+10)', true),
           (200, 2, 'S-200', 'Script #200', 2, 'print(10+10)', false);

INSERT INTO T_WORKFLOW(id, tenant_fk, name)
    VALUES(100, 1, 'w1');

INSERT INTO T_ACTIVITY(id, tenant_fk, workflow_fk, name, script_fk)
    VALUES (110, 1, 100, 'START', 110);
