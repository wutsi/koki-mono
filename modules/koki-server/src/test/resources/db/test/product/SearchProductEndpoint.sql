INSERT INTO T_PRODUCT(id, tenant_fk, type, code, name, description, active, deleted)
       VALUES (100, 1, 4, 'RAY-123', 'Rayband 123', 'Glasses with class', true, false),
              (110, 1, 2, 'XXX', 'PRoduct !', null, true, false),
              (120, 1, 2, 'XXX', 'PRoduct !', null, false, false),
              (130, 1, 3, 'XXX', 'PRoduct !', null, true, false),
              (140, 1, 3, 'XXX', 'PRoduct !', null, false, false),
              (199, 1, 2, 'XXX', 'PRoduct !', null, true, true),
              (200, 2, 2, 'XXX', 'PRoduct !', null, true, false);
