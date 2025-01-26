INSERT INTO T_EMAIL(id, tenant_fk, sender_fk, recipient_fk, recipient_type, subject, body, summary)
    VALUES (100, 1, 111, 100, 1, 'hello', '<p>World</p>', 'X'),
           (200, 2, 111, 100, 1, 'hello', '<p>World</p>', 'Y');
