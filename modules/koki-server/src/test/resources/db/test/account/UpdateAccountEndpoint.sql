INSERT INTO T_LOCATION(id, parent_fk, type, name, ascii_name, country)
    VALUES (111, 333, 3, 'Montreal', 'Montreal', 'CA'),
           (222, 777, 3, 'Quebec',   'Quebec', 'CA');

INSERT INTO T_ATTRIBUTE(id, tenant_fk, name, label, description, choices, type, active)
    VALUES (100, 1, 'neq', 'NEQ', null, null, 1, true),
           (101, 1, 'tps', 'TPS', null, null, 1, true),
           (102, 1, 'tvq', 'TVQ', null, null, 1, false),
           (103, 1, 'client_since', 'Client Since', null, null, 4, false),
           (104, 1, 'new_client', 'New Client', null, null, 9, false);

INSERT INTO T_TYPE(id, tenant_fk, object_type, name, title, active)
    VALUES (100, 1, 1, 'T1', 'Tier 1', true),
           (101, 1, 1, 'T2', 'Tier 2', true),
           (102, 1, 1, 'T4', null, true);

INSERT INTO T_ACCOUNT(id, tenant_fk, name, email, deleted)
    VALUES(1000, 1, 'Inc', 'info@inc1.com', false),
          (1200, 1, 'Inc', 'info@inc2.com', false),
          (1999, 1, 'Inc', 'info@inc3.com', true),
          (2000, 2, 'Inc', 'info@inc4.com', false);

INSERT INTO T_ACCOUNT_ATTRIBUTE(account_fk, attribute_fk, value)
    VALUES (1000, 100, 'NE-00000'),
           (1000, 101, 'TPS-11111'),
           (1000, 103, '2024'),
           (1000, 104, 'yes');
