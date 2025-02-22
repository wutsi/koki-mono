INSERT INTO T_LOCATION (id, parent_fk, type, name, ascii_name, country)
    VALUES (100, null, 2, 'Quebec',   'Quebec',   'CA'),
           (111, 100,  3, 'Montreal', 'Montreal', 'CA');

INSERT INTO T_JURIDICTION (id, state_fk, country)
    VALUES (1010, 111,  'CA'),
           (1011, null, 'CA');

INSERT INTO T_TENANT(id, name, domain_name, locale, currency, portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', 'https://tenant-1.com');

INSERT INTO T_BUSINESS(id, tenant_fk, company_name)
    VALUES (100, 1, 'My Business');

INSERT INTO T_BUSINESS_JURIDICTION(business_fk, juridiction_fk)
    VALUES (100, 1010);
