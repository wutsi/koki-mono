INSERT INTO T_LISTING_LOCATION_METRIC(
    id, tenant_fk, neighbourhood_fk, property_category, listing_type, listing_status,
    total_listings, min_price, max_price, average_price, average_lot_area, price_per_square_meter, total_price, currency
) VALUES
    (100, 1, 1000, 1, 1, 6, 10, 100000, 500000, 300000, 500, 600.0, 3000000, 'XAF'),
    (101, 1, 1000, 1, 2, 6, 5, 50000, 150000, 100000, 400, 250.0, 500000, 'XAF'),
    (102, 1, 1001, 1, 1, 6, 8, 200000, 600000, 400000, 600, 666.67, 3200000, 'XAF'),
    (103, 1, 1002, 2, 1, 6, 12, 150000, 450000, 300000, 800, 375.0, 3600000, 'XAF'),
    (104, 1, 1003, 3, 1, 6, 6, 80000, 200000, 140000, 300, 466.67, 840000, 'XAF');


