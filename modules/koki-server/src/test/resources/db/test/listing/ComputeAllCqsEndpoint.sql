INSERT INTO T_LISTING(id, tenant_fk, status, content_quality_score)
    VALUES
        (100, 1, 6, null),   -- ACTIVE
        (101, 1, 6, 75),     -- ACTIVE with existing score
        (102, 1, 7, null),   -- ACTIVE_WITH_CONTINGENCIES
        (103, 1, 8, null),   -- SOLD
        (104, 1, 0, null),   -- DRAFT (should be skipped)
        (200, 2, 6, null);   -- Different tenant

INSERT INTO T_FILE(id, tenant_fk, owner_fk, owner_type, type, name, content_type, content_length, status, image_quality, url, deleted)
    VALUES
        (1000, 1, 100, 4, 1, 'a.png', 'image/png', 1000, 1, 4, 'https://example.com/img1.jpg', false),  -- APPROVED, HIGH
        (1001, 1, 100, 4, 1, 'a.png', 'image/png', 1000, 1, 3, 'https://example.com/img2.jpg', false),  -- APPROVED, MEDIUM
        (1002, 1, 100, 4, 2, 'a.txt', 'text/plan', 1000, 0, 4, 'https://example.com/img3.jpg', false),  -- PENDING, HIGH (ignored)
        (1003, 1, 100, 4, 2, 'a.pdf', 'application/pdf', 1000, 2, 4, 'https://example.com/img4.jpg', false),  -- REJECTED (ignored)
        (1004, 1, 102, 4, 2, 'a.pdf', 'application/pdf', 1000, 1, 2, 'https://example.com/img5.jpg', false),  -- APPROVED, LOW
        (1005, 1, 102, 4, 2, 'a.txt', 'text/plan', 1000, 1, 1, 'https://example.com/img6.jpg', false);  -- APPROVED, POOR
