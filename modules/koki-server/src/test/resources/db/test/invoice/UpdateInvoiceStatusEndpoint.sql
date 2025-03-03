INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, created_at, logo_url, icon_url, portal_url)
    VALUES (1, 1, 'test', 'test.com', 'en_CA', '#,###,###.#0', 'CAD', 'CA$', 'CA$ #,###,###.#0', 'yyyy-MM-dd', 'HH:mm', 'yyyy-MM-dd HH:mm', '2020-01-22 12:30', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'https://test.com');

INSERT INTO T_INVOICE(id, tenant_fk, tax_fk, order_fk, number, status, customer_account_fk, customer_name, customer_email, amount_due, currency)
    VALUES
        (100, 1, null, null, 10955, 1, 1,    'Ray Sponsible', 'ray.sponsible@gmail.com', 0, 'CAD'),
        (101, 1, null, null, 10956, 1, 1,    'Ray Sponsible', 'ray.sponsible@gmail.com', 0, 'CAD'),
        (102, 1, null, null, 10957, 1, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (103, 1, null, null, 10958, 1, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (104, 1, null, null, 10959, 1, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (110, 1, null, null, 10910, 2, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (111, 1, null, null, 10911, 2, null, 'Roger', 'roger.milla@gmail.com', 10, 'CAD'),
        (112, 1, null, null, 10912, 2, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (113, 1, null, null, 10913, 2, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (120, 1, null, null, 10920, 3, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (121, 1, null, null, 10921, 3, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (122, 1, null, null, 10922, 3, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (130, 1, null, null, 10930, 4, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (131, 1, null, null, 10931, 4, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),
        (132, 1, null, null, 10932, 4, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (140, 1, 1400, null, 10940, 2, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD'),

        (200, 2, null, null, 10957, 1, null, 'Roger', 'roger.milla@gmail.com', 0, 'CAD')
;

INSERT INTO T_TAX(id, tenant_fk, account_fk, invoice_fk, fiscal_year)
    VALUES (1400, 1, 100, 140, 2024);

