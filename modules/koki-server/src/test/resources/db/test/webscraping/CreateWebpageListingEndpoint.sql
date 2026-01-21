INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, created_at, logo_url, icon_url, portal_url, client_portal_url, country)
    VALUES (1, 1, 'test', 'test.com', 'en_CA', '#,###,###.#0', 'CAD', 'CA$', 'CA$ #,###,###.#0', 'yyyy-MM-dd', 'HH:mm', 'yyyy-MM-dd HH:mm', '2020-01-22 12:30', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'https://test.com','https://client.tenant-1.com', 'CA');

INSERT INTO T_LOCATION (id, parent_fk, type, country, name, ascii_name)
    VALUES (1000,null,1, 'CM', 'Cameroon', 'Cameroon'),
           (1100,1000,2, 'CM', 'Centre', 'Centre'),
           (1110,1100,3, 'CM', 'Yaounde', 'yaounde'),
           (1111,1110,4, 'CM', 'Bastos', 'bastos');

INSERT INTO T_CATEGORY(id, type, level, name, long_name, active)
    VALUES (1100, 1, 0, 'A',  'A', true),
           (1200, 1, 0, 'B',  'B', true),
           (1300, 1, 0, 'C',  'C', true),
           (1400, 1, 0, 'D',  'C', true);

INSERT INTO T_AMENITY(id, category_fk, name, active)
    VALUES (1101, 1100, 'AA1', true),
           (1102, 1100, 'AA2', true),
           (1103, 1100, 'AA3', false),
           (1201, 1200, 'BB1', false),
           (1202, 1200, 'BB2', true);

INSERT INTO T_WEBSITE(id, tenant_fk, user_fk, base_url, base_url_hash, listing_url_prefix, content_selector, image_selector, active, created_at)
    VALUES
        (100, 1, 11, 'https://example.com', '5d41402abc4b2a76b9719d911017c592', 'https://example.com/listings/', '.content', 'img.gallery', true, now());

INSERT INTO T_WEBPAGE(id, tenant_fk, website_fk, listing_fk, url, url_hash, content, image_urls, active, created_at)
    VALUES
        (100, 1, 100, null, 'https://example.com/listings/1', 'c4ca4238a0b923820dcc509a6f75849b', 'Webpage Content', 'https://picsum.photos/100/300,https://picsum.photos/100', true, now()),
        (101, 1, 100, null, 'https://example.com/listings/2', 'c4ca4238a0b923820dcc509a6f75849c', null, 'https://picsum.photos/200/300,https://picsum.photos/100', true, now()),
        (102, 1, 100, null, 'https://example.com/listings/3', 'c4ca4238a0b923820dcc509a6f7584ff', '', 'https://picsum.photos/200/300,https://picsum.photos/100', true, now()),
        (103, 1, 100, null, 'https://example.com/listings/4', 'c4ca4238a0b923820dcc509a6f758433', ' ', 'https://picsum.photos/200/300,https://picsum.photos/100', true, now()),
        (104, 1, 100, null, 'https://example.com/listings/5', 'c4ca4238a0b923820dcc509a6f758444', 'invalid image', 'https://picsum.photos/104/300,https://picsum.photos/104', true, now()),
        (105, 1, 100, null, 'https://example.com/listings/6', 'c4ca4238a0b923820dcc509a6f758477', 'invalid city', null, true, now()),
        (106, 1, 100, 106, 'https://example.com/listings/7', 'c4ca4238a0b923820dcc509a6f758488', 'invalid city', null, true, now());

INSERT INTO T_LISTING(id, tenant_fk, status, listing_type, property_type, listing_number)
    VALUES (106, 1, 3, 1, 2, 1000000);
