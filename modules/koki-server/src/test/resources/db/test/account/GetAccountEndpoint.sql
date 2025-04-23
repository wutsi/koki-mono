INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title, active)
    VALUES (100, 1, 1, 'T1', 'Tier 1', true),
           (101, 1, 1, 'T2', 'Tier 2', true),
           (102, 1, 1, 'T4', null, true);

INSERT INTO T_ATTRIBUTE(id, tenant_fk, name, label, description, choices, type, active)
    VALUES (100, 1, 'neq', 'NEQ', null, null, 1, true),
           (101, 1, 'tps', 'TPS', null, null, 1, true),
           (102, 1, 'tvq', 'TVQ', null, null, 1, false),
           (103, 1, 'client_since', 'Client Since', null, null, 4, false)
;

INSERT INTO T_ACCOUNT(id, tenant_fk, account_type_fk, name, phone, mobile, email, website, language, description, deleted, created_by_fk, modified_by_fk, managed_by_fk, account_user_fk, invitation_fk)
    VALUES(1000, 1, 101, 'Inc', '+5147580000', '+5147580011', 'info@inc.com', 'https://www.inc.com', 'fr', 'This is the description of account', false, 11, 12, 13, 333, 'aaaa-bbbb'),
          (1010, 1, null, 'Sponsible', null, null, null, null, null, null, false, null, null, null, null, null),
          (1999, 1, 100, 'Deleted', null, null, null, null, null, null, true, null, null, null, null, null),
          (2000, 2, null, 'Inc', null, null, null, null, null, null, false, null, null, null, null, null);

INSERT INTO T_ACCOUNT_ATTRIBUTE(account_fk, attribute_fk, value)
    VALUES (1000, 100, 'NEQ-00000'),
           (1000, 101, 'TPS-11111');
