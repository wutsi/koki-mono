INSERT INTO T_TENANT(id, status, name, domain_name, locale, country, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, logo_url, icon_url, portal_url, client_portal_url) VALUES
    (1, 1, 'BlueKoki', 'koki-portal-test-5bc3b457d6f5.herokuapp.com', 'fr-CM', 'CM', '#,###,##0.00', 'XAF', 'XAF', '#,###,##0 FCFA', 'yyyy-MM-dd', 'hh:mm a', 'yyyy-MM-dd hh:mm a', 'https://com-wutsi-koki-test.s3.us-east-1.amazonaws.com/tenant/1/logo/logo.png', 'https://com-wutsi-koki-test.s3.us-east-1.amazonaws.com/tenant/1/logo/icon.png', 'https://koki-portal-test-5bc3b457d6f5.herokuapp.com', 'https://koki-portal-public-test-9ed11f1892bd.herokuapp.com');

INSERT INTO T_TENANT_MODULE(tenant_fk, module_fk)
    SELECT 1, id FROM T_MODULE;

INSERT INTO T_ROLE(id, tenant_fk, name) VALUES
    (1, 1, 'Administrator'),
    (2, 1, 'Realtor');

DELETE FROM T_ROLE_PERMISSION;
INSERT INTO T_ROLE_PERMISSION(role_fk, permission_fk) SELECT 1, P.id from T_PERMISSION P;

INSERT INTO T_USER(id, tenant_fk, username, email, password, display_name, status, salt)
    VALUES (1,  1, 'herve.tchepannou', 'herve.tchepannou@gmail.com', '607e0b9e5496964b1385b7c10e3e2403', 'Herve Tchepannou', 3, '...143.,..');

INSERT INTO T_USER_ROLE values(1, 1);

INSERT INTO T_CONFIGURATION (tenant_fk, name, value) VALUES
    (1, 'listing.start.number', '250000'),
    (1, 'role.agent_id', '2')
;
