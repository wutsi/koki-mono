INSERT INTO T_OFFER(id, tenant_fk, owner_fk, owner_type, seller_agent_user_fk, buyer_agent_user_fk, buyer_contact_fk, status)
    VALUES (100, 1, 111,  1,    11,  22,  1,   1),
           (101, 1, null, null, 11,  22, 1, 1),
           (102, 1, null, null, 333, 222, 1, 1),
           (103, 1, null, null, 333, 222, 1, 2);

INSERT INTO T_OFFER_VERSION(id, tenant_fk, offer_fk, assignee_user_fk, submitting_party, status, price, currency)
    VALUES  (1000, 1, 100, null, 1, 1, 10000, 'CAD'),
            (1001, 1, 101, 7777, 1, 1, 10000, 'CAD'),
            (1002, 1, 102, null, 1, 1, 10000, 'CAD'),
            (1003, 1, 103, 7777, 1, 1, 10000, 'CAD');

UPDATE T_OFFER set version_fk=1000 where id=100;
UPDATE T_OFFER set version_fk=1001 where id=101;
UPDATE T_OFFER set version_fk=1002 where id=102;
UPDATE T_OFFER set version_fk=1003 where id=103;
