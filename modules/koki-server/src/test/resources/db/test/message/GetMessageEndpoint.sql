INSERT INTO T_MESSAGE(id, tenant_fk, owner_fk, owner_type, sender_email, sender_name, sender_phone, body, status, country, language, sender_account_fk)
    VALUE (100, 1, 11, 1, 'ray.sponsible@gmail.com', 'Ray Sponsible', '5147580011', 'Yo man', 2, 'CA', 'fr', 111),
          (200, 2, 11, 1, 'ray.sponsible@gmail.com', 'Ray Sponsible', '5147580011', 'Yo man', 2, null, null, null);
