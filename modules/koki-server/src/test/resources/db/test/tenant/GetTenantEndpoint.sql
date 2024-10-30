
INSERT INTO T_USER(id, email, password, display_name) VALUE(11, 'ray.sponsible@gmail.com', '---', 'Ray Sponsible');

INSERT INTO T_TENANT(id, owner_fk, name, domain_name, locale, currency, created_at)
    VALUES(1, 11, 'test', 'test.com', 'en_US', 'USD', '2020-01-22 12:30');
