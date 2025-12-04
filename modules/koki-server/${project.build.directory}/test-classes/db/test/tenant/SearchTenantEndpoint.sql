INSERT T_MODULE(id, name, title, description, home_url, tab_url, settings_url)
    VALUES
        (101, 'MODULE1', 'Module 1', 'This is a module', '/module1', '/module1/tab', '/settings/module1'),
        (102, 'MODULE2', 'Module 2', 'This is a module', '/module2', null, null),
        (103, 'MODULE3', 'Module 3', 'This is a module', '/module3', '/module1/tab', '/settings/module1');


INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, logo_url, icon_url, portal_url, client_portal_url, country)
    VALUES
        (1, 1, 'test', 'localhost', 'fr_FR', '#,###,###',    'CAD', 'CA$',  'CA$ #,###,###.#0', 'yyyy-MM-dd', 'hh:mm a', 'yyyy-MM-dd hh:mm a', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'http://localhost:8081', 'http://localhost:8082', 'CA'),
        (2, 1, 'A',    'test.ca',   'en_CA', '#,###,###.#0', 'CAD', 'CA$',  'CA$ #,###,###.#0', 'yyyy-MM-dd', 'HH:mm',   'yyyy-MM-dd HH:mm', null, null, 'https://test.ca', 'https://client.test.ca', 'CA'),
        (3, 1, 'B',    'test.com',  'en_US', '#,###,###.#0', 'USD', '$',    '$ #,###,###.#0',   'yyyy-MM-dd', 'hh:mm a', 'yyyy-MM-dd hh:mm a', null, null, 'https://test.com', 'https://client.test.ca', 'CA'),
        (4, 2, 'C',    'test.fr',   'fr_FR', '#,###,###.#0', 'EUR', '€',    '€ #,###,###.#0',   'yyyy-MM-dd', 'HH:mm',   'yyyy-MM-dd HH:mm', null, null, 'https://test.fr', 'https://client.test.ca', 'CA'),
        (5, 1, 'D',    'test.cm',   'fr_FR', '#,###,###',    'XAF', 'FCFA', '#,###,### FCFA',   'yyyy-MM-dd', 'HH:mm',   'yyyy-MM-dd HH:mm', null, null, 'https://test.cm', 'https://client.test.ca', 'CA')
;

INSERT INTO T_TENANT_MODULE(tenant_fk, module_fk)
    VALUES
        (1, 101),
        (1, 102),
        (1, 103),
        (2, 101),
        (2, 102);
