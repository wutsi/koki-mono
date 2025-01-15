INSERT INTO T_ACCOUNT(id, tenant_fk, name, email)
    VALUES(100, 1, 'Ray Inc', 'info@ray-inc.com'),
          (101, 1, 'No email', null);

INSERT INTO T_CONTACT(id, tenant_fk, account_fk, first_name, last_name, email)
    VALUES(110, 1, 100, 'Ray', 'Sponsible', 'ray.sponsible@gmail.com'),
          (120, 1, 100, 'No', 'Email', '');

