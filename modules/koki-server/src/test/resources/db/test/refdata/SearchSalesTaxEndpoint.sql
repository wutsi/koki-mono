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

INSERT INTO T_SALES_TAX(id, country, state_fk, name, rate, active)
    VALUES(1011, 'CA', 101, 'GST', 5.0, true),
          (1091, 'CA', 109, 'HST', 13.0, true),
          (1111, 'CA', 111, 'GST', 5.0, true),
          (1112, 'CA', 111, 'PST', 9.975, true),
          (2001, 'CM', 111, 'VAT', 19.75, true),
          (2002, 'CM', 111, 'VAT-old', 30.0, false);
