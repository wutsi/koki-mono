INSERT INTO T_PRODUCT(id, tenant_fk, type, code, name, description, active, deleted)
       VALUES (100, 1, 1, 'RAY-123', 'Rayband 123', 'Glasses with class', true, false),
              (110, 1, 2, 'P-123', 'Product 123', null, true, false),
              (120, 1, 2, 'P-3t3', 'Product 333', null, false, false),
              (130, 1, 3, 'R-000', 'Rolce Royce', null, true, false),
              (140, 1, 3, 'P-XXX', 'Porshe zzzz', null, false, false),
              (199, 1, 2, 'P-YYY', 'PRoduct !', null, true, true),
              (200, 2, 2, 'XXX', 'PRoduct !', null, true, false);
