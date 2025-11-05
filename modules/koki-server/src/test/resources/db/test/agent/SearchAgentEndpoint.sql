INSERT INTO T_AGENT(id, tenant_fk, user_fk, total_sales, total_rentals, past_12m_sales, past_12m_rentals)
    VALUES(100, 1, 11, 100, 200, 50, 100),
          (200, 2, 22, 100, 200, 100, 200);

INSERT INTO T_AGENT_METRIC(tenant_fk, agent_fk, listing_type, period, total, min_price, max_price, average_price, total_price, currency)
    VALUES
        (1, 100, 1, 1, 11, 1500, 15000, 12500, 115000, 'CAD'),
        (1, 100, 1, 2, 10, 500, 5000, 2500, 15000, 'CAD');
