INSERT INTO T_WEBSITE(id, tenant_fk, user_fk, base_url, base_url_hash, listing_url_prefix, content_selector, image_selector, active, created_at)
    VALUES
        (100, 1, 11, 'https://example.com', '5d41402abc4b2a76b9719d911017c592', 'https://example.com/listings/', '.content', 'img.gallery', true, now());

INSERT INTO T_WEBPAGE(id, tenant_fk, website_fk, url, url_hash, content, image_urls, active, created_at)
    VALUES
        (200, 1, 100, 'https://example.com/listings/1', 'c4ca4238a0b923820dcc509a6f75849b', 'Listing 1 content', '["https://example.com/img1.jpg"]', true, now()),
        (201, 1, 100, 'https://example.com/listings/2', 'c81e728d9d4c2f636f067f89cc14862c', 'Listing 2 content', '["https://example.com/img2.jpg", "https://example.com/img3.jpg"]', true, now()),
        (202, 1, 100, 'https://example.com/listings/3', 'eccbc87e4b5ce2fe28308fd9f2a7baf3', 'Listing 3 content', '[]', false, now());

