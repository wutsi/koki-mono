INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, logo_url, icon_url, portal_url)
    VALUES
        (1, 1, 'test', 'localhost', 'fr_FR', '#,###,###',    'CAD', 'CA$',  'CA$ #,###,###.#0', 'yyyy-MM-dd', 'hh:mm a', 'yyyy-MM-dd hh:mm a', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'http://localhost:8081'),
        (2, 1, 'A',    'test.ca',   'en_CA', '#,###,###.#0', 'CAD', 'CA$',  'CA$ #,###,###.#0', 'yyyy-MM-dd', 'HH:mm',   'yyyy-MM-dd HH:mm', null, null, 'https://test.ca'),
        (3, 1, 'B',    'test.com',  'en_US', '#,###,###.#0', 'USD', '$',    '$ #,###,###.#0',   'yyyy-MM-dd', 'hh:mm a', 'yyyy-MM-dd hh:mm a', null, null, 'https://test.com'),
        (4, 2, 'C',    'test.fr',   'fr_FR', '#,###,###.#0', 'EUR', '€',    '€ #,###,###.#0',   'yyyy-MM-dd', 'HH:mm',   'yyyy-MM-dd HH:mm', null, null, 'https://test.fr'),
        (5, 1, 'D',    'test.cm',   'fr_FR', '#,###,###',    'XAF', 'FCFA', '#,###,### FCFA',   'yyyy-MM-dd', 'HH:mm',   'yyyy-MM-dd HH:mm', null, null, 'https://test.cm')
;
