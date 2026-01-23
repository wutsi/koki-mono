INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'fr_CM', 'CM', 'XAF', 'https://tenant-1.com', 'https://client.tenant-1.com');

INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES (1000,null,1, 'CM', 'Cameroon', 'Cameroon'),
           (1100,1000,2, 'CM', 'Centre', 'Centre'),
           (1110,1100,3, 'CM', 'Yaounde', 'yaounde'),
           (1111,1110,4, 'CM', 'Bastos', 'bastos');

INSERT INTO T_CATEGORY(id, type, level, name, long_name, active)
    VALUES (1100, 1, 0, 'A',  'A', true),
           (1200, 1, 0, 'B',  'B', true),
           (1300, 1, 0, 'C',  'C', true),
           (1400, 1, 0, 'D',  'C', true);

INSERT INTO T_AMENITY(id, category_fk, name, active)
    VALUES (1101, 1100, 'AA1', true),
           (1102, 1100, 'AA2', true),
           (1103, 1100, 'AA3', false),
           (1201, 1200, 'BB1', false),
           (1202, 1200, 'BB2', true);

INSERT INTO T_USER(id, tenant_fk, username, email, display_name, street, city_fk, salt, password)
    VALUES (333, 1, 'agent1', 'ray@gmail.com', 'Ray Sponsible', '3030 Linton', 1110, '--', '---');
