INSERT INTO T_CATEGORY(id, parent_fk, type, level, name, long_name, active)
    VALUES (1100, null, 1, 0, 'A',  'A', true),
           (1110, 1100, 1, 1, 'B1', 'A > B1', true),
           (1120, 1100, 1, 1, 'B2', 'A > B2', true),
           (1130, 1100, 1, 1, 'B3', 'A > B3', true),
           (1131, 1130, 1, 2, 'C1', 'A > B3 > C1', true),
           (1132, 1130, 1, 2, 'C2', 'A > B3 > C2', true),
           (2100, null, 2, 0, 'X',  'X', true),
           (2110, 2100, 2, 1, 'Y1', 'X > Y1', false),
           (2120, 2100, 2, 1, 'Y2', 'X > Y2', false);
