INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number, seller_agent_user_fk)
    VALUES (111, 1, 3, 1, 2, 1000000, null);

INSERT INTO T_LEAD(id, tenant_fk, listing_fk, user_fk, first_name, last_name, email, phone_number, status)
    VALUES (100, 1, 111, 1, 'Yo', 'Man', 'yo.man@gmail.com', '+15477580000', 1);
