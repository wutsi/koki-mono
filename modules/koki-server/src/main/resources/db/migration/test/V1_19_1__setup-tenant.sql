INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, logo_url, icon_url, portal_url, client_portal_url) VALUES
    (1, 1, 'wutsi', 'koki-portal-test-5bc3b457d6f5.herokuapp.com', 'CA', '#,###,##0.00', 'CAD', 'C$', 'C$ #,###,##0.00', 'yyyy-MM-dd', 'hh:mm a', 'yyyy-MM-dd hh:mm a', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'https://koki-portal-test-5bc3b457d6f5.herokuapp.com', 'https://koki-client-portal-test-d6781109715a.herokuapp.com');

INSERT INTO T_TENANT_MODULE(tenant_fk, module_fk)
    SELECT 1, id FROM T_MODULE;

INSERT INTO T_ROLE(id, tenant_fk, name) VALUES(1, 1, 'Administrator');

INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk) SELECT 1, P.id from T_PERMISSION P;

INSERT INTO T_USER(id, tenant_fk, type, username, email, password, display_name, status, salt)
    VALUES (1,  1, 1, 'herve.tchepannou', 'herve.tchepannou@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Herve Tchepannou', 1, '...143.,..');

INSERT INTO T_USER_ROLE values(1, 1);
