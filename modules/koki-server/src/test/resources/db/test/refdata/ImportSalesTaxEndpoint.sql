INSERT INTO T_LOCATION (id, type, name, ascii_name, country)
    VALUES (100, 1, 'Canada', 'Canada', 'CA'),
           (101, 2, 'Alberta', 'Alberta', 'CA'),
           (102, 2, 'British Columbia', 'British Columbia', 'CA'),
           (103, 2, 'Manitoba', 'Manitoba', 'CA'),
           (104, 2, 'New Brunswick', 'New Brunswick', 'CA'),
           (105, 2, 'Newfoundland and Labrador', 'Newfoundland and Labrador', 'CA'),
           (106, 2, 'Northwest Territories', 'Northwest Territories', 'CA'),
           (107, 2, 'Nova Scotia', 'Nova Scotia', 'CA'),
           (108, 2, 'Nunavut', 'Nunavut', 'CA'),
           (109, 2, 'Ontario', 'Ontario', 'CA'),
           (110, 2, 'Prince Edward Island', 'Prince Edward Island', 'CA'),
           (111, 2, 'Quebec', 'Quebec', 'CA'),
           (112, 2, 'Saskatchewan', 'Saskatchewan', 'CA'),
           (113, 2, 'Yukon', 'Yukon', 'CA')
;

INSERT INTO T_JURIDICTION (id, state_fk, country)
    VALUES (1000, null, 'CA'),
           (1001, 101, 'CA'),
           (1002, 102, 'CA'),
           (1003, 103, 'CA'),
           (1004, 104, 'CA'),
           (1005, 105, 'CA'),
           (1006, 106, 'CA'),
           (1007, 107, 'CA'),
           (1008, 108, 'CA'),
           (1009, 109, 'CA'),
           (1010, 110, 'CA'),
           (1011, 111, 'CA'),
           (1012, 112, 'CA'),
           (1013, 113, 'CA'),

           (237, null, 'CM')
;

INSERT INTO T_SALES_TAX(id, juridiction_fk, name, rate, active)
    VALUES(100, 1011, 'XXX', 9.0, true);
