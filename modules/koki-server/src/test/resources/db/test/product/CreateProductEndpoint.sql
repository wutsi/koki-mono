INSERT INTO T_UNIT(id, name, abbreviation)
    VALUES (11, 'Hours', 'hr'),
           (12, 'Sessopm', null);

INSERT INTO T_PRODUCT(id, tenant_fk, code, name, description, active, deleted)
       VALUES (100, 1, 'XXX', 'PRoduct !', null, true, false),
              (199, 1, 'XXX', 'PRoduct !', null, true, true),
              (200, 2, 'XXX', 'PRoduct !', null, true, false);
