INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec',   'CA'),
           (111, 100,  3, 'Montreal', 'Montreal', 'CA');

INSERT INTO T_JURIDICTION (id, state_fk, country)
    VALUES (1010, 111,  'CA'),
           (1011, null, 'CA');

INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com','https://client.tenant-1.com');

INSERT INTO T_BUSINESS(id, tenant_fk, company_name, phone, fax, email, website, address_city_fk, address_state_fk, address_country, address_postal_code, address_street)
    VALUES (100, 1, 'Business Inc', '+5147580100', '+5147580111', 'info@my-biz.com', 'https://my-biz.com', 111, 100, 'CA', 'H7K1C6', '340 Pascal');

INSERT INTO T_BUSINESS_JURIDICTION(business_fk, juridiction_fk)
    VALUES (100, 1010),
           (100, 1011);
