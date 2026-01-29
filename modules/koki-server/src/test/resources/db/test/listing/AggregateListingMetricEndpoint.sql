-- Test data for AggregateListingMetricEndpoint
-- Insert some listings that will be aggregated into metrics

INSERT INTO T_LISTING(
    id, tenant_fk, status, listing_type, property_category,
    neighbourhood_fk, city_fk, seller_agent_user_fk, bedrooms,
    price, currency, lot_area, property_area,
    created_at, modified_at
) VALUES
    -- Neighbourhood 1000, Agent 100, City 10, RESIDENTIAL, SALE, SOLD
    (1000, 1, 3, 1, 1, 1000, 10, 100, 3, 100000, 'XAF', 500, 100, NOW(), NOW()),
    (1001, 1, 3, 1, 1, 1000, 10, 100, 3, 200000, 'XAF', 600, 120, NOW(), NOW()),
    (1002, 1, 3, 1, 1, 1000, 10, 100, 3, 300000, 'XAF', 700, 150, NOW(), NOW()),

    -- Neighbourhood 1000, Agent 100, City 10, RESIDENTIAL, RENTAL, SOLD
    (1003, 1, 3, 2, 1, 1000, 10, 100, 2, 50000, 'XAF', 400, 80, NOW(), NOW()),
    (1004, 1, 3, 2, 1, 1000, 10, 100, 2, 75000, 'XAF', 450, 90, NOW(), NOW()),

    -- Neighbourhood 1001, Agent 101, City 10, RESIDENTIAL, SALE, ACTIVE
    (1005, 1, 3, 1, 1, 1001, 10, 101, 4, 250000, 'XAF', 800, 130, NOW(), NOW()),
    (1006, 1, 3, 1, 1, 1001, 10, 101, 4, 350000, 'XAF', 900, 160, NOW(), NOW()),

    -- Neighbourhood 1002, Agent 102, City 11, LAND, SALE, SOLD
    (1007, 1, 3, 1, 2, 1002, 11, 102, NULL, 180000, 'XAF', 1000, 0, NOW(), NOW()),
    (1008, 1, 3, 1, 2, 1002, 11, 102, NULL, 220000, 'XAF', 1200, 0, NOW(), NOW()),

    -- Neighbourhood 1003, Agent 103, City 11, COMMERCIAL, SALE, SOLD
    (1009, 1, 3, 1, 3, 1003, 11, 103, NULL, 500000, 'XAF', 500, 200, NOW(), NOW());

