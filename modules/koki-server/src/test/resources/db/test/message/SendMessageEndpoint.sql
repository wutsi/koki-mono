INSERT INTO T_LOCATION (id, parent_fk, country, name, ascii_name, type)
    VALUES (237,     null,  'CM', 'Cameroon', 'Cameroon', 1),
           (23701,   237,   'CM', 'Centre', 'Centre', 2),
           (23702,   237,   'CM', 'Ouest', 'Ouest', 2),
           (2370201, 23702, 'CM', 'Bafoussam', 'bafoussam', 3),
           (2370202, 23702, 'CM', 'Bafang', 'bafang', 3),
           (2370103, 23702, 'CM', 'Bafou', 'Bafou', 3),
           (2370104, 23702, 'CM', 'Bana', 'Bana', 3),
           (2370101, 23701, 'CM', 'Yaounde', 'yaounde', 3),
           (2370102, 23701, 'CM', 'Obala', 'obala', 3),
           (1,       null,  'CA', 'Canada', 'canada', 1),
           (1514,    1,     'CA', 'Quebec', 'quebec', 2),
           (151401,  1515,  'CA', 'Montreal', 'montreal', 3)
;

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, language, shipping_city_fk, shipping_country, mobile)
    VALUES(100, 1, 'Inc', 'info@inc.com', null, null, null, null),
          (110, 1, 'FooBar', 'info@foobar.com', null, null, null, null),
          (120, 1, 'YoMan', 'info@yoman.com', 'fr', 2370102, 'CM', '+18001111111');



