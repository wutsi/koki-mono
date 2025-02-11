INSERT INTO T_LOCATION (id, parent_fk, country, name, ascii_name)
    VALUES (237,     null,  'CM', 'Cameroon', 'Cameroon'),
           (23701,   237,   'CM', 'Centre', 'Centre'),
           (23702,   237,   'CM', 'Ouest', 'Ouest'),
           (2370201, 23702, 'CM', 'Bafoussam', 'bafoussam'),
           (2370202, 23702, 'CM', 'Bafang', 'bafang'),
           (2370103, 23702, 'CM', 'Bafou', 'Bafou'),
           (2370104, 23702, 'CM', 'Bana', 'Bana'),
           (2370101, 23701, 'CM', 'Yaounde', 'yaounde'),
           (2370102, 23701, 'CM', 'Obala', 'obala'),
           (1,       null,  'CA', 'Canada', 'canada'),
           (1514,    1,     'CA', 'Quebec', 'quebec'),
           (151401,  1515,  'CA', 'Montreal', 'montreal')
;
