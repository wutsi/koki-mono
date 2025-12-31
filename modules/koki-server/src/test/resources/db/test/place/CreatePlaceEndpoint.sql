INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country, latitude, longitude)
    VALUES (111, null, 3, 'Montréal', 'Montreal', 'CA', null, null),
           (222, 111, 4, 'Côte-des-Neiges', 'Cote-des-Neiges', 'CA', 45.4972159, -73.6390246),
           (333, 111, 4, 'Westmont', 'Westmont', 'CA', 45.4972159, -73.6390246),
           (444, 111, 4, 'Auteuil', 'auteuil', 'CA', 45.1, -73.1);

INSERT INTO T_PLACE(id, created_by_fk, name, ascii_name, type, status, neighbourhood_fk, city_fk, deleted)
    VALUES (333, 11, 'Westmount', 'westmount', 1, 1, 333, 111, false);


