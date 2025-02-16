INSERT INTO T_PRODUCT(id, tenant_fk, type, code, name, description, active, deleted, category_fk)
       VALUES (100, 1, 1, 'RAY-123', 'Rayband 123', 'Glasses with class', true, false, 222),
              (199, 1, 2, 'XXX', 'PRoduct !', null, true, true, null),
              (200, 2, 2, 'XXX', 'PRoduct !', null, true, false, null);
