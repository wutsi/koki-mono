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

-- Insert an existing school to test update scenario
INSERT INTO T_PLACE(id, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, city_fk, deleted, private, international, website_url)
    VALUES (200001, null, 'American International School of Yaounde (AISOY)', 'american international school of yaounde (aisoy)',
            2, 1, 100003, 6297005, false, false, false, 'https://www.old-url.org/');

