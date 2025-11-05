INSERT INTO T_TENANT(id, name, domain_name, locale, country, currency, portal_url, client_portal_url)
    VALUES (1, 'tenant-1', 'tenant-1.com', 'en_US', 'CA', 'CAD', 'https://tenant-1.com', 'https://client.tenant-1.com');

INSERT INTO T_AGENT(id, tenant_fk, user_fk, last_sold_at)
    VALUES(11, 1, 11, date_add(current_date(), INTERVAL -3 HOUR)),
          (22, 1, 22, date_add(current_date(), INTERVAL -3 HOUR));

INSERT INTO T_LISTING(listing_number, tenant_fk, listing_type, seller_agent_user_fk, buyer_agent_user_fk, sold_at, sale_price, currency)
    VALUES
        (1000, 1, 1, 11, 22,   date_add(current_date(), INTERVAL -1 YEAR), 200000, 'CAD'),
        (1001, 1, 1, 11, 11,   date_add(current_date(), INTERVAL -1 YEAR), 300000, 'CAD'),
        (1002, 1, 2, 11, null, date_add(current_date(), INTERVAL -1 YEAR), 1000, 'CAD'),

        (1100, 1, 1, 11, null, date_add(current_date(), INTERVAL -1 DAY), 100000, 'CAD'),
        (1101, 1, 1, 11, 11,   date_add(current_date(), INTERVAL -1 DAY), 150000, 'CAD'),
        (1102, 1, 2, 22, 11,   date_add(current_date(), INTERVAL -1 DAY), 500, 'CAD'),
        (1103, 1, 2, 11, 22,   date_add(current_date(), INTERVAL -1 DAY), 2000, 'CAD')
    ;
