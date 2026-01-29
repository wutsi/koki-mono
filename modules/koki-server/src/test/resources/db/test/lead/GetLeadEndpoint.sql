INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type)
    VALUES (111, 1, 3, 1, 2);

INSERT INTO T_LEAD(id, tenant_fk, listing_fk, device_id, user_fk, agent_user_fk, status, next_visit_at, next_contact_at)
    VALUES (100, 1, 111, 'xxx', 11, 222, 2, '2026-12-30', '2026-11-30'),
           (200, 2, 222, null, 1, 222, 1, '2026-12-30', null);

INSERT INTO T_LEAD_MESSAGE(id, tenant_fk, lead_fk, content)
    VALUES (1000, 1, 100,  'Initial message from user');

UPDATE T_LEAD set last_message_fk=1000 where id=100;
