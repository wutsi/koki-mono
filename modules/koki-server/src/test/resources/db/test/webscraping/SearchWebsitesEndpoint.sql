INSERT INTO T_WEBSITE(id, tenant_fk, user_fk, base_url, base_url_hash, listing_url_prefix, home_urls, content_selector, image_selector, active, created_at)
    VALUES
        (100, 1, 11, 'https://example.com', '5d41402abc4b2a76b9719d911017c592', 'https://example.com/listings/', '["https://example.com?page=1"]', '.content', 'img.gallery', true, '2020-01-22'),
        (101, 1, 11, 'https://test.com', '098f6bcd4621d373cade4e832627b4f6', 'https://test.com/properties/', '[]', '.description', 'img.photo', true, '2020-01-23'),
        (102, 1, 12, 'https://another.com', 'a87ff679a2f3e71d9181a67b7542122c', 'https://another.com/items/', '[]', null, null, false, '2020-01-24'),
        (103, 1, 12, 'https://inactive.com', 'c4ca4238a0b923820dcc509a6f75849b', 'https://inactive.com/posts/', '[]', '.text', 'img.main', false, '2020-01-25'),
        (104, 1, 13, 'https://user13.com', 'c81e728d9d4c2f636f067f89cc14862c', 'https://user13.com/ads/', '[]', '.body', 'img.hero', true, '2020-01-26');

