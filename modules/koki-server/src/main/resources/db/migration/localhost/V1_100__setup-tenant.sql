INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, logo_url, icon_url, portal_url) VALUES
    (1, 1, 'test', 'localhost', 'fr_FR', '#,###,###',    'CAD', 'CA$',  'CA$ #,###,###.#0', 'yyyy-MM-dd', 'hh:mm a', 'yyyy-MM-dd hh:mm a', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'http://localhost:8081');

INSERT INTO T_TENANT_MODULE(tenant_fk, module_fk)
    VALUES (1, 100),
           (1, 110),
           (1, 120),
           (1, 130),
           (1, 140),
           (1, 150),
           (1, 160);

INSERT INTO T_ROLE(id, tenant_fk, name, title) VALUES(1, 1, 'ADM', 'Administrator');

INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk) SELECT 1, P.id from T_PERMISSION P;

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status, salt)
    VALUES (1,  1, 'herve.tchepannou@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Herve Tchepannou', 1, '...143.,..');

INSERT INTO T_USER_ROLE values(1, 1);
