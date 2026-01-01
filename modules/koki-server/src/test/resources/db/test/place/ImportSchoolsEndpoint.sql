-- Import Schools Endpoint Test Fixture
-- Creates necessary locations (cities and neighbourhoods) for school import testing

-- Insert Cameroon as country
INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES (2233387, null, 1, 'CM', 'Cameroon', 'Cameroon');

-- Insert Centre state
INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES (2233376, 2233387, 2, 'CM', 'Centre', 'Centre');

-- Insert Yaoundé city
INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES (6297005, 2233376, 3, 'CM', 'Yaoundé', 'Yaounde');

-- Insert neighbourhoods referenced in schools.csv
INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES
        (100001, 6297005, 4, 'CM', 'Bastos', 'Bastos'),
        (100002, 6297005, 4, 'CM', 'Golf', 'Golf'),
        (100003, 6297005, 4, 'CM', 'Quartier Lac', 'Quartier Lac'),
        (100004, 6297005, 4, 'CM', 'Ngoa-Ekélé', 'Ngoa-Ekele'),
        (100005, 6297005, 4, 'CM', 'Obili', 'Obili'),
        (100006, 6297005, 4, 'CM', 'Centre Ville', 'Centre Ville'),
        (100007, 6297005, 4, 'CM', 'Emana', 'Emana'),
        (100008, 6297005, 4, 'CM', 'Messassi', 'Messassi'),
        (100009, 6297005, 4, 'CM', 'Mvog-Mbi', 'Mvog-Mbi'),
        (100010, 6297005, 4, 'CM', 'Etoug-Ebe', 'Etoug-Ebe'),
        (100011, 6297005, 4, 'CM', 'Odza', 'Odza'),
        (100012, 6297005, 4, 'CM', 'Mvolyé', 'Mvolye'),
        (100013, 6297005, 4, 'CM', 'Etoudi', 'Etoudi'),
        (100014, 6297005, 4, 'CM', 'Nkolbisson', 'Nkolbisson'),
        (100015, 6297005, 4, 'CM', 'Soa', 'Soa'),
        (100016, 6297005, 4, 'CM', 'Essos', 'Essos'),
        (100017, 6297005, 4, 'CM', 'Mendong', 'Mendong'),
        (100018, 6297005, 4, 'CM', 'Djoungolo', 'Djoungolo'),
        (100019, 6297005, 4, 'CM', 'Biteng', 'Biteng'),
        (100020, 6297005, 4, 'CM', 'Biyem-Assi', 'Biyem-Assi'),
        (100021, 6297005, 4, 'CM', 'Melen', 'Melen'),
        (100022, 6297005, 4, 'CM', 'Mimboman', 'Mimboman'),
        (100024, 6297005, 4, 'CM', 'Tsinga', 'Tsinga');

-- Insert an existing school to test update scenario
INSERT INTO T_PLACE(id, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, city_fk, deleted, private, international, website_url)
    VALUES (200001, null, 'American International School of Yaounde (AISOY)', 'american international school of yaounde (aisoy)',
            2, 1, 100003, 6297005, false, false, false, 'https://www.old-url.org/');

