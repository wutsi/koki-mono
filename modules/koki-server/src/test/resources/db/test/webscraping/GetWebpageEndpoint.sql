INSERT INTO T_WEBSITE(id, tenant_fk, user_fk, base_url, base_url_hash, listing_url_prefix, content_selector, image_selector, active, created_at)
    VALUES
        (100, 1, 11, 'https://example.com', '5d41402abc4b2a76b9719d911017c592', 'https://example.com/listings/', '.content', 'img.gallery', true, now());

INSERT INTO T_WEBPAGE(id, tenant_fk, website_fk, url, url_hash, content, image_urls, active, created_at)
    VALUES
        (200, 1, 100, 'https://example.com/listings/1', 'c4ca4238a0b923820dcc509a6f75849b', 'Listing 1 content with detailed description', 'https://example.com/img1.jpg,https://example.com/img2.jpg', true, now());

