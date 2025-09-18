INSERT INTO T_OFFER(id, tenant_fk, owner_fk, owner_type, seller_agent_user_fk, buyer_agent_user_fk, buyer_contact_fk, version_fk, status, total_versions)
    VALUES (100, 1, 333, 1, 11, 22, 33, null, 1, 3),
           (200, 2, 333, 1, 11, 22, 33, null, 1, 3);

INSERT INTO T_OFFER_VERSION(id, tenant_fk, offer_fk, created_by_fk, submitting_party, status, price, currency, contingencies)
    VALUES  (111, 1, 100, 1, 1, 1, 10000, 'CAD', 'This is a contingency');

UPDATE T_OFFER set version_fk=111 where id=100;
