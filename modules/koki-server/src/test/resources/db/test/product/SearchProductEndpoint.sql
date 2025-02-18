INSERT INTO T_PRODUCT(id, tenant_fk, type, code, name, description, active, deleted)
       VALUES (100, 1, 1, 'RAY-123', 'Rayband 123', 'Glasses with class', true, false),
              (110, 1, 2, 'XXX', 'Product 123', null, true, false),
              (120, 1, 2, 'XXX', 'Product 333', null, false, false),
              (130, 1, 3, 'XXX', 'Rolce Royce', null, true, false),
              (140, 1, 3, 'XXX', 'Porshe zzzz', null, false, false),
              (199, 1, 2, 'XXX', 'PRoduct !', null, true, true),
              (200, 2, 2, 'XXX', 'PRoduct !', null, true, false);
