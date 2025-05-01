INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec',  'CA'),
           (110, 100,  3, 'Montreal', 'Montrel', 'CA'),
           (200, null, 2, 'Ontario',  'Ontario', 'CA'),
           (210, 200,  3, 'Toronto',  'Toronto', 'CA');

INSERT INTO T_JURIDICTION (id, state_fk, country)
    VALUES (1000, null, 'CA'),
           (1001, 100,  'CA');

INSERT INTO T_SALES_TAX(id, juridiction_fk, name, rate, active)
    VALUES(10, 1000, 'GST', 5.0, true),
          (20, 1001, 'GST', 5.0, true),
          (21, 1001, 'PST', 9.975, true);

INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com', 'https://client.tenant-1.com');

INSERT INTO T_BUSINESS(id, tenant_fk, company_name, phone, fax, email, website, address_city_fk, address_state_fk, address_country, address_postal_code, address_street)
    VALUES (1, 1, 'My Business', '+5147580100', '+5147580111', 'info@my-biz.com', 'https://my-biz.com', 111, 100, 'CA', 'H7K1C6', '340 Pascal');

INSERT INTO T_BUSINESS_JURIDICTION(business_fk, juridiction_fk)
    VALUES (1, 1000),
           (1, 1001);

INSERT INTO T_INVOICE_SEQUENCE(tenant_fk, current) VALUES(1, 10943);

INSERT INTO T_TAX(id, tenant_fk, account_fk, fiscal_year)
    VALUES (111, 1, 100, 2024);
