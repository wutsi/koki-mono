INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.test.com'),
           (2, 'tenant-2', 'tenant-2.com', 'en_US', 'US', 'USD', 'https://tenant-1.com', 'https://client.test.com');

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
           (11, 1, 'writer'),
           (12, 1, 'reader'),
           (20, 2, 'accountant'),
           (21, 2, 'technician');

INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk)
    VALUES (11, 101);


INSERT INTO T_USER(id, tenant_fk, username, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1),
           (12, 1, 'john.smith', 'john.smith@gmail.com', '---', 'John Smith', 1),
           (13, 1, 'raymond-rougeau','raymond-rougeau@gmail.com', '---', 'Raymond Rougeau', 2),
           (14, 1, 'hulk','hulk@gmail.com', '---', 'Hulk Hogan', 4),
           (15, 1, 'pp','pp@gmail.com', '---', 'Peter Pan', 1),
           (16, 1, 'spiderman','spiderman@gmail.com', '---', 'Peter Parker', 1),
           (17, 1, 'pf1969','pf1969@gmail.com', '---', 'Peter Fonda', 1),
           (18, 1, 'pf1970','pf1970@gmail.com', '---', 'Henry Fonda', 1),

           (22, 2, 'roger.milla','roger.milla@gmail.com', '---', 'Roger Milla', 1),
           (23, 2, 'user.23', 'user.23@gmail.com', '---', 'AccountUser 23', 1)
;

INSERT INTO T_USER_ROLE(user_fk, role_fk)
    VALUES (11, 10),
           (11, 11),
           (12, 11),
           (22, 20);
