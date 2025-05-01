INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title)
    VALUES (100, 1, 6, 'PERSONAL', 'Personal Taxes');

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, managed_by_fk)
    VALUES (111, 1, 'Account 1', 'ray1@gmail.com', 11);
