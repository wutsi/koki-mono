INSERT INTO T_WEBSITE(id, tenant_fk, user_fk, base_url, base_url_hash, listing_url_prefix, content_selector, image_selector, active, created_at)
    VALUES
        (103, 2, 12, 'https://tenant2.com', '8277e0910d750195b448797616e091ad', 'https://tenant2.com/listings/', '.text', 'img', true, now()),
        (102, 1, 11, 'https://inactive.com', 'c4ca4238a0b923820dcc509a6f75849b', 'https://inactive.com/homes/', null, null, false, now()),
        (101, 1, 11, 'https://test.com', '098f6bcd4621d373cade4e832627b4f6', 'https://test.com/properties/', '.description', 'img.photo', true, now()),
        (100, 1, 11, 'https://example.com', 'c984d06aafbecf6bc55569f964148ea3', 'https://example.com/listings/', '.content', 'img.gallery', true, now());

