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
