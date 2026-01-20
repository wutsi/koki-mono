INSERT INTO T_WEBSITE(id, tenant_fk, user_fk, base_url, base_url_hash, listing_url_prefix, content_selector, image_selector, active, created_at)
    VALUES
        (100, 1, 11, 'https://example.com', '5d41402abc4b2a76b9719d911017c592', 'https://example.com/listings/', '.content', 'img.gallery', true, now());

