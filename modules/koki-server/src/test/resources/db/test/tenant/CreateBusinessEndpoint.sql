INSERT INTO T_LOCATION (id, type, name, ascii_name, country)
    VALUES (111, 2, 'Quebec', 'Quebec', 'CA');

INSERT INTO T_JURIDICTION (id, state_fk, country)
    VALUES (1010, 111, 'CA'),
           (1011, null, 'CA');

INSERT INTO T_SALES_TAX(id, juridiction_fk, name, rate, active)
    VALUES(1111, 1010, 'GST', 5.0, true),
          (1112, 1010, 'PST', 9.975, true);

INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com');

