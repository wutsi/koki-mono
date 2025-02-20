INSERT INTO T_LOCATION (id, type, name, ascii_name, country)
    VALUES (101, 2, 'Alberta', 'Alberta', 'CA'),
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
    VALUES (1000, 101, 'CA'),
           (1001, 102, 'CA'),
           (1002, 103, 'CA'),
           (1003, 104, 'CA'),
           (1004, 105, 'CA'),
           (1005, 106, 'CA'),
           (1006, 107, 'CA'),
           (1007, 108, 'CA'),
           (1008, 109, 'CA'),
           (1009, 110, 'CA'),
           (1010, 111, 'CA'),
           (1011, 112, 'CA'),
           (1012, 113, 'CA'),

           (237, null, 'CM')
;

INSERT INTO T_SALES_TAX(id, juridiction_fk, name, rate, active)
    VALUES(1011, 1000, 'GST', 5.0, true),
          (1091, 1008, 'HST', 13.0, true),
          (1111, 1010, 'GST', 5.0, true),
          (1112, 1010, 'PST', 9.975, true),
          (2001, 237,  'VAT', 19.75, true),
          (2002, 237,  'VAT-old', 30.0, false);
