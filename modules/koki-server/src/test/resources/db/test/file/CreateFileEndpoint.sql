INSERT INTO T_TENANT(id, name, domain_name, locale, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'USD', '$', '$#,###,##0', 'dd MM yyyy', 'HH:mm', 'dd MMM yyyy, HH:mm');

INSERT INTO T_USER(id, tenant_fk, email, password, display_name, status)
    VALUES (11, 1, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible', 1)
