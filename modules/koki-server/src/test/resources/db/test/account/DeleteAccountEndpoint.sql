INSERT INTO T_ACCOUNT(id, tenant_fk, name, deleted, email)
    VALUES(1000, 1, 'Inc',     false, 'info@inc.com'),
          (1100, 1, 'with contact', false, 'info@yo.com'),
          (1110, 1, 'with taxes', false,'info@tax.com'),
          (1999, 1, 'Inc',     true, '1320932093-info@tax.com'),
          (2000, 2, 'Inc',     false, 'info@inc.com');

INSERT INTO T_CONTACT(tenant_fk, account_fk, first_name, last_name)
    VALUES(1, 1100, 'Ray', 'Sponsible');

INSERT INTO T_TAX(tenant_fk, account_fk)
    VALUES(1, 1110);
