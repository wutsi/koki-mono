INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number, seller_agent_user_fk)
    VALUES (111, 1, 3, 1, 2, 1000000, null);

INSERT INTO T_LEAD(id, tenant_fk, listing_fk, agent_user_fk, user_fk, status)
    VALUES (100, 1, 111, 222,1, 1);
