-- Import Markets Endpoint Test Fixture
-- Creates necessary locations (cities and neighbourhoods) for market import testing

-- Insert Cameroon as country
INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES (2233387, null, 1, 'CM', 'Cameroon', 'Cameroon');

-- Insert Centre state
INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES (2233376, 2233387, 2, 'CM', 'Centre', 'Centre');

-- Insert Yaoundé city
INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES (6297005, 2233376, 3, 'CM', 'Yaoundé', 'Yaounde');


-- Insert an existing market to test update scenario
INSERT INTO T_PLACE(id, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, city_fk, deleted, international, website_url, rating)
    VALUES (400001, null, 'DOVV Bastos', 'dovv-bastos',
            6, 1, 300001, 6297005, false, false, 'https://old-url.com', 3.0);

