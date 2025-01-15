INSERT INTO T_EMAIL(id, tenant_fk, sender_fk, recipient_fk, recipient_type, subject, body, created_at)
    VALUES (100, 1, 111, 100, 1, 'hello', '<p>World</p>', '2020-01-01'),
           (101, 1, 111, 100, 1, 'hello', '<p>World</p>', '2020-01-02'),
           (102, 1, 111, 100, 1, 'hello', '<p>World</p>', '2020-01-03'),
           (103, 1, 111, 100, 1, 'hello', '<p>World</p>', '2020-01-04'),
           (104, 1, 111, 100, 1, 'hello', '<p>World</p>', '2020-01-05'),
           (200, 2, 111, 100, 1, 'hello', '<p>World</p>', '2020-01-01');

INSERT INTO T_EMAIL_OWNER(email_fk, owner_fk, owner_type)
    VALUES (100, 111, 1),
           (103, 111, 1);
