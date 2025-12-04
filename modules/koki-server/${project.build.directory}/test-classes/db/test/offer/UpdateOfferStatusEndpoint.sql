INSERT INTO T_OFFER(id, tenant_fk, owner_fk, owner_type, seller_agent_user_fk, buyer_agent_user_fk, buyer_contact_fk, version_fk, status, total_versions)
    VALUES (100, 1, 333, 1, 11,  22,  33, null, 1, 3),
           (101, 1, 333, 1, 11,  22,  33, null, 1, 3),
           (102, 1, 333, 1, 11,  22,  33, null, 1, 3),
           (103, 1, 333, 1, 11,  22,  33, null, 1, 3),
           (200, 1, 333, 1, 11,  22,  33, null, 2, 3),
           (201, 1, 333, 1, 11,  22,  33, null, 2, 3),
           (110, 1, 333, 1, 111, 22,  33, null, 2, 3);

INSERT INTO T_OFFER_VERSION(id, tenant_fk, offer_fk, created_by_fk, submitting_party, status, price, currency, contingencies)
    VALUES  (1000, 1, 100, 1, 1, 1, 10000, 'CAD', 'This is a contingency'),
            (1010, 1, 101, 1, 1, 1, 10000, 'CAD', null),
            (1020, 1, 102, 1, 1, 1, 10000, 'CAD', null),
            (1030, 1, 102, 1, 1, 1, 10000, 'CAD', null),
            (2000, 1, 200, 1, 1, 2, 10000, 'CAD', null),
            (2010, 1, 201, 1, 1, 2, 10000, 'CAD', null),
            (1100, 1, 110, 1, 1, 1, 10000, 'CAD', null);

UPDATE T_OFFER set version_fk=1000 where id=100;
UPDATE T_OFFER set version_fk=1010 where id=101;
UPDATE T_OFFER set version_fk=1020 where id=102;
UPDATE T_OFFER set version_fk=1030 where id=103;
UPDATE T_OFFER set version_fk=2000 where id=200;
UPDATE T_OFFER set version_fk=2010 where id=201;
