INSERT INTO T_LOCATION (id, type, parent_fk, country, name, ascii_name)
    VALUES (237,       1, null,  'CM', 'Cameroon', 'Cameroon'),
           (23701,     2, 237,   'CM', 'Centre', 'Centre'),
           (23702,     2, 237,   'CM', 'Ouest', 'Ouest'),
           (2370201,   3, 23702, 'CM', 'Bafoussam', 'bafoussam'),
           (2370202,   3, 23702, 'CM', 'Bafang', 'bafang'),
           (2370103,   3, 23702, 'CM', 'Bafou', 'Bafou'),
           (2370104,   3, 23702, 'CM', 'Bana', 'Bana'),
           (2370101,   3, 23701, 'CM', 'Yaounde', 'yaounde'),
           (2370102,   3, 23701, 'CM', 'Obala', 'obala'),
           (237010100, 4, 23701, 'CM', 'Bastos', 'bastos'),
           (237010101, 4, 23701, 'CM', 'Essos', 'essos'),
           (237010102, 4, 23701, 'CM', 'Mokolo', 'Mokolo'),
           (237010200, 4, 23702, 'CM', 'Bepanda', 'Bepanda'),
           (237010201, 4, 23702, 'CM', 'Bonandjo', 'Bonandjo'),
           (1,         1, null,  'CA', 'Canada', 'canada'),
           (1514,      2, 1,     'CA', 'Quebec', 'quebec'),
           (151401,    3, 1515,  'CA', 'Montreal', 'montreal')
;

INSERT INTO T_ROOM_LOCATION_METRIC(tenant_fk, location_fk, total_published_rentals)
    VALUES (1, 2370101,   10),
           (1, 2370102,   11),
           (1, 2370103,   20),
           (1, 237010100, 5),
           (1, 237010101, 7),
           (1, 151401,    13);

