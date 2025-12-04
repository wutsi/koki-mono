INSERT INTO T_MESSAGE(id, tenant_fk, owner_fk, owner_type, sender_email, sender_name, sender_phone, body, status)
    VALUE (100, 1, 11,   1,    'ray.sponsible@gmail.com', 'Ray Sponsible', '5147580011', 'Yo man', 2),
          (101, 1, null, null, 'ray.sponsible@gmail.com', 'Ray Sponsible', '5147580011', 'Yo man', 1),
          (102, 1, 11,   1,    'ray.sponsible@gmail.com', 'Ray Sponsible', '5147580011', 'Yo man', 1),
          (200, 2, null, null, 'ray.sponsible@gmail.com', 'Ray Sponsible', '5147580011', 'Yo man', 2);
