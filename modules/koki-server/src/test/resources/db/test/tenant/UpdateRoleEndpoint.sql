INSERT T_MODULE(id, name, title, description, home_url, tab_url, settings_url)
    VALUES
        (100, 'MODULE1', 'Module 1', 'This is a module', '/module1', '/module1/tab', '/settings/module1'),
        (200, 'MODULE2', 'Module 1', 'This is a module', '/module1', '/module1/tab', '/settings/module1'),
        (300, 'MODULE3', 'Module 1', 'This is a module', '/module1', '/module1/tab', '/settings/module1');


INSERT T_PERMISSION(id, module_fk, name, description)
    VALUES
        (101, 100, 'module1:read', 'Read data'),
        (102, 100, 'module1:status', null),
        (201, 200, 'module2', 'Read data'),
        (202, 200, 'module2:admin', null),
        (301, 300, 'module2:admin', null);

INSERT INTO T_ROLE(id, tenant_fk, name)
    VALUES (10, 1, 'admin'),
           (20, 1, 'role20'),
           (30, 1, 'role30');

INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk)
    VALUES (10, 101);
