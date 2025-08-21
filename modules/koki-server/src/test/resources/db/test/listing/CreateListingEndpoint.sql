INSERT INTO T_CONFIGURATION (tenant_fk, name, value)
    VALUES (1, 'listing.start.number', '250000');

INSERT INTO T_LISTING_SEQUENCE(tenant_fk, current)
    VALUES (1, 10);
