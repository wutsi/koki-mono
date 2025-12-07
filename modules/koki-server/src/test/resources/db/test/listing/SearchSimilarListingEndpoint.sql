-- Test data for similar listings endpoint
-- Reference listing: ID 100 - 3 bedroom house, $500,000
INSERT INTO T_LISTING(id, tenant_fk, listing_number, city_fk, neighbourhood_fk, listing_type, property_type, furniture_type, status, bedrooms, bathrooms, price, sale_price, lot_area, property_area, seller_agent_user_fk, buyer_agent_user_fk, created_at)
VALUES
    -- Reference listing: 3 bedroom house at $500,000
    (100, 1, 1000000, 1001, 2001, 1, 1, null, 3, 3, 2, 500000, null, 5000, 2000, 1001, null, current_timestamp),

    -- Perfect match (should score 1.0)
    (101, 1, 1010000, 1001, 2001, 1, 1, null, 3, 3, 2, 500000, null, 5000, 2000, 1001, null, current_timestamp),

    -- Same type, same bedrooms, slightly higher price (should score ~0.92)
    (102, 1, 1020000, 1001, 2002, 1, 1, null, 0, 3, 2, 550000, null, 5500, 2200, 1002, null, current_timestamp),

    -- Same type, 1 bedroom difference, same price (should score 0.85)
    (103, 1, 1030000, 1002, 2001, 1, 1, null, 0, 4, 2, 500000, null, 6000, 2400, 1001, null, current_timestamp),

    -- Different type (apartment), same bedrooms, same price (should score 0.5)
    (104, 1, 1040000, 1001, 2003, 1, 2, null, 0, 3, 2, 500000, null, 1500, 1500, 1003, null, current_timestamp),

    -- Same type, same bedrooms, price outside 25% range (should score 0.8)
    (105, 1, 1050000, 1002, 2002, 1, 1, null, 0, 3, 2, 650000, null, 7000, 2800, 1002, null, current_timestamp),

    -- Same type, 2 bedroom difference, same price (should score 0.7)
    (106, 1, 1060000, 1001, 2001, 1, 1, null, 0, 5, 3, 500000, null, 7500, 3000, 1001, null, current_timestamp),

    -- LAND type (should not match with house)
    (107, 1, 1070000, 1001, 2001, 1, 4, null, 0, null, null, 500000, null, 10000, null, 1001, null, current_timestamp),

    -- COMMERCIAL type (should not match with house)
    (108, 1, 1080000, 1002, 2003, 1, 5, null, 0, null, null, 500000, null, 8000, 8000, 1002, null, current_timestamp),

    -- INDUSTRIAL type (should not match with house)
    (109, 1, 1090000, 1002, 2002, 1, 6, null, 0, null, null, 500000, null, 15000, 15000, 1003, null, current_timestamp),

    -- Same type, same bedrooms, within price range, SOLD status
    (110, 1, 1100000, 1001, 2001, 1, 1, null, 6, 3, 2, 600000, 480000, 5200, 2100, 1001, 2001, current_timestamp),

    -- Studio (should match with other residential types except LAND/COMMERCIAL/INDUSTRIAL)
    (111, 1, 1110000, 1001, 2002, 1, 3, null, 0, 1, 1, 400000, null, 800, 800, 1002, null, current_timestamp),

    -- Another house, different city, same everything else
    (112, 1, 1120000, 1003, 2004, 1, 1, null, 0, 3, 2, 500000, null, 5000, 2000, 1004, null, current_timestamp),

    -- House with same agent
    (113, 1, 1130000, 1002, 2003, 1, 1, null, 0, 3, 2, 520000, null, 5300, 2150, 1001, null, current_timestamp),

    -- LAND reference for testing exclusive matching
    (200, 1, 2000000, 1001, 2001, 1, 4, null, 0, null, null, 300000, null, 20000, null, 1001, null, current_timestamp),

    -- Another LAND (should match with 200)
    (201, 1, 2010000, 1001, 2002, 1, 4, null, 0, null, null, 320000, null, 22000, null, 1002, null, current_timestamp),

    -- COMMERCIAL reference
    (300, 1, 3000000, 1001, 2001, 1, 5, null, 0, null, null, 800000, null, 10000, 10000, 1001, null, current_timestamp),

    -- Another COMMERCIAL (should match with 300)
    (301, 1, 3010000, 1002, 2002, 1, 5, null, 0, null, null, 850000, null, 11000, 11000, 1002, null, current_timestamp),

    -- Different tenant (should not appear in results)
    (900, 2, 9000000, 1001, 2001, 1, 1, null, 0, 3, 2, 500000, null, 5000, 2000, 1001, null, current_timestamp)
;

