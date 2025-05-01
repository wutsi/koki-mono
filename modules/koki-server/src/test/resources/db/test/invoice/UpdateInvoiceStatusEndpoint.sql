INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, created_at, logo_url, icon_url, portal_url, client_portal_url)
    VALUES (1, 1, 'test', 'test.com', 'en_CA', '#,###,###.#0', 'CAD', 'CA$', 'CA$ #,###,###.#0', 'yyyy-MM-dd', 'HH:mm', 'yyyy-MM-dd HH:mm', '2020-01-22 12:30', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'https://test.com', 'https://client.test.com');

INSERT INTO T_BUSINESS(id, tenant_fk, company_name, phone, fax, email, website, address_city_fk, address_state_fk, address_country, address_postal_code, address_street)
    VALUES (100, 1, 'Business Inc', '+5147580100', '+5147580111', 'info@my-biz.com', 'https://my-biz.com', 111, 100, 'CA', 'H7K1C6', '340 Pascal');


INSERT INTO T_INVOICE(id, paynow_id, tenant_fk, tax_fk, order_fk, number, status, customer_account_fk, customer_name, customer_email, amount_due, currency)
    VALUES
        (100, 'paynow100', 1, null, null, 10955, 1, 1,    'Ray Sponsible', 'ray.sponsible@gmail.com', 0, 'CAD'),
        (101, 'paynow101', 1, null, null, 10956, 1, 1,    'Ray Sponsible', 'ray.sponsible@gmail.com', 0, 'CAD'),
        (102, 'paynow102', 1, null, null, 10957, 1, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (103, 'paynow103', 1, null, null, 10958, 1, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (104, 'paynow104', 1, null, null, 10959, 1, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (110, 'paynow110', 1, null, null, 10910, 2, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (111, 'paynow111', 1, null, null, 10911, 2, null, 'Roger', 'roger.milla@gmail.com', 10, 'CAD'),
        (112, 'paynow112', 1, null, null, 10912, 2, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (113, 'paynow113', 1, null, null, 10913, 2, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (120, 'paynow120', 1, null, null, 10920, 3, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (121, 'paynow121', 1, null, null, 10921, 3, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (122, 'paynow122', 1, null, null, 10922, 3, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (130, 'paynow130', 1, null, null, 10930, 4, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (131, 'paynow131', 1, null, null, 10931, 4, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (132, 'paynow132', 1, null, null, 10932, 4, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (140, 'paynow140', 1, 1400, null, 10940, 2, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (200, 'paynow200', 2, null, null, 10957, 1, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD')
;

INSERT INTO T_TAX(id, tenant_fk, account_fk, invoice_fk, fiscal_year)
    VALUES (1400, 1, 100, 140, 2024);

