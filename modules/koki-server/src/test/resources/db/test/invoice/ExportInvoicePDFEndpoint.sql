INSERT INTO T_UNIT(id, name)
    VALUES (110, 'Hour'),
           (111, 'Day'),
           (112, 'Week'),
           (113, 'Month'),
           (120, 'Session'),
           (121, 'Class'),
           (122, 'Consultation'),
           (130, 'Project'),
           (131, 'Website'),
           (132, 'Design'),
           (140, 'Visit'),
           (141, 'Treatment'),
           (142, 'Lesson');

INSERT INTO T_LOCATION (id, type, name, ascii_name, country)
    VALUES (101, 2, 'Alberta', 'Alberta', 'CA'),
           (102, 2, 'British Columbia', 'British Columbia', 'CA'),
           (103, 2, 'Manitoba', 'Manitoba', 'CA'),
           (104, 2, 'New Brunswick', 'New Brunswick', 'CA'),
           (105, 2, 'Newfoundland and Labrador', 'Newfoundland and Labrador', 'CA'),
           (106, 2, 'Northwest Territories', 'Northwest Territories', 'CA'),
           (107, 2, 'Nova Scotia', 'Nova Scotia', 'CA'),
           (108, 2, 'Nunavut', 'Nunavut', 'CA'),
           (109, 2, 'Ontario', 'Ontario', 'CA'),
           (110, 2, 'Prince Edward Island', 'Prince Edward Island', 'CA'),
           (111, 2, 'Quebec', 'Quebec', 'CA'),
           (112, 2, 'Saskatchewan', 'Saskatchewan', 'CA'),
           (113, 2, 'Yukon', 'Yukon', 'CA'),
           (200, 1, 'Cameroon', 'Cameron', 'C<')
;

INSERT INTO T_JURIDICTION(id, country, state_fk)
    VALUES(1000, 'CA', 101),
          (1008, 'CA', 109),
          (1010, 'CA', 111),
          (237, 'CM', null);

INSERT INTO T_SALES_TAX(id, juridiction_fk, name, rate, active)
    VALUES(1011, 1000, 'GST', 5.0, true),
          (1091, 1008, 'HST', 13.0, true),
          (1111, 1010, 'GST', 5.0, true),
          (1112, 1010, 'PST', 9.975, true),
          (2001, 237,  'VAT', 19.75, true),
          (2002, 237,  'VAT-old', 30.0, false);

INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, created_at, logo_url, icon_url, portal_url, client_portal_url)
    VALUES (1, 1, 'test', 'test.com', 'en_CA', '#,###,###.#0', 'CAD', 'CA$', 'C$ #,###,##0.00', 'yyyy-MM-dd', 'HH:mm', 'yyyy-MM-dd HH:mm', '2020-01-22 12:30', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'https://test.com', 'https://client.test.com');

INSERT INTO T_BUSINESS(id, tenant_fk, company_name, phone, fax, email, website, address_city_fk, address_state_fk, address_country, address_postal_code, address_street)
    VALUES (100, 1, 'My Business', '+5147580100', '+5147580111', 'info@my-biz.com', 'https://my-biz.com', 111, 100, 'CA', 'H9H 9H9', '3030 Linton');

INSERT INTO T_INVOICE(
    id, paynow_id, tenant_fk, order_fk,
    number, status, description,
    sub_total_amount, total_tax_amount, total_discount_amount, total_amount, amount_paid, amount_due, currency,
    customer_account_fk, customer_name, customer_email, customer_phone, customer_mobile,
    shipping_street, shipping_postal_code, shipping_city_fk, shipping_state_fk, shipping_country,
    billing_street, billing_postal_code, billing_city_fk, billing_state_fk, billing_country,
    invoiced_at, due_at)
  VALUES
      (
        100, 'paynow100', 1, 9999,
        100, 2, 'Sample description',
        800.00, 40.00, 20.00, 820.00, 810.00, 10.00, 'CAD',
        111, 'Ray Sponsible', 'ray.sponsible@gmail.com', '+5147580111', '+514758000',
        '340 Pascal', 'H1K1C1', 111, 100, 'CA',
        '311 Pascal', 'H2K2C2', 211, 200, 'CA',
        '2025-01-01', '2025-01-30'
      ),
      (
        101, 'paynow101', 1, null,
        101, 1, 'Draft invoice',
        800.00, 40.00, 20.00, 820.00, 810.00, 10.00, 'CAD',
        111, 'Ray Sponsible', 'ray.sponsible@gmail.com', '+5147580111', '+514758000',
        '340 Pascal', 'H1K1C1', 111, 100, 'CA',
        '311 Pascal', 'H2K2C2', 211, 200, 'CA',
        '2025-01-01', '2025-01-30'
      ),
      (
        102, 'paynow102', 1, null,
        102, 4, 'Voided invoice',
        800.00, 40.00, 20.00, 820.00, 810.00, 10.00, 'CAD',
        111, 'Ray Sponsible', 'ray.sponsible@gmail.com', '+5147580111', '+514758000',
        '340 Pascal', 'H1K1C1', 111, 100, 'CA',
        '311 Pascal', 'H2K2C2', 211, 200, 'CA',
        '2025-01-01', '2025-01-30'
      ),

      (
          200, 'paynow200', 1, 9999,
          10956, 3, 'Sample description',
          800.00, 40.00, 20.00, 820.00, 810.00, 10.00, 'CAD',
          111, 'Ray Sponsible', 'ray.sponsible@gmail.com', '+5147580111', '+514758000',
          '340 Pascal', 'H1K1C1', 111, 100, 'CA',
          '311 Pascal', 'H2K2C2', 211, 200, 'CA',
          '2025-01-01', '2025-01-01'
      ),
      (
          300, 'paynow300', 1, 9999,
          10957, 4, 'Sample description',
          800.00, 40.00, 20.00, 820.00, 810.00, 10.00, 'CAD',
          111, 'Ray Sponsible', 'ray.sponsible@gmail.com', '+5147580111', '+514758000',
          '340 Pascal', 'H1K1C1', 111, 100, 'CA',
          '311 Pascal', 'H2K2C2', 211, 200, 'CA',
          '2025-01-01', '2025-01-01'
        );

INSERT INTO T_INVOICE_ITEM(id, invoice_fk, product_fk, unit_price_fk, unit_fk, quantity, unit_price, sub_total, currency, description)
VALUES (110, 100, 1, 11, 110, 2, 300, 600, 'CAD', 'product 1'),
       (120, 100, 2, 22, 111, 1, 200, 200, 'CAD', 'product 2'),

       (112, 102, 1, 11, 110, 2, 300, 600, 'CAD', 'product 1'),
       (122, 102, 2, 22, 111, 1, 200, 200, 'CAD', 'product 2'),

       (210, 200, 1, 11, 110, 2, 300, 600, 'CAD', 'product 1'),
       (220, 200, 2, 22, 111, 1, 200, 200, 'CAD', 'product 2');

INSERT INTO T_INVOICE_TAX(id, invoice_item_fk, sales_tax_fk, rate, amount, currency)
VALUES (1101, 110, 1011, 5.000, 10.00, 'CAD'),
       (1102, 110, 1112, 9.975, 25.00, 'CAD'),
       (1201, 120, 1011, 5.000,  5.00, 'CAD'),

       (1121, 112, 1011, 5.000, 10.00, 'CAD'),
       (1122, 112, 1112, 9.975, 25.00, 'CAD'),
       (1221, 122, 1011, 5.000,  5.00, 'CAD'),

       (211, 210, 1011, 5.000, 10.00, 'CAD'),
       (212, 210, 1112, 9.975, 25.00, 'CAD'),
       (221, 220, 1011, 5.000,  5.00, 'CAD');

INSERT INTO T_PRODUCT(id, tenant_fk, code, name, description, active)
       VALUES (1, 1, 'T1',     'Product 123', null, true),
              (2, 1, 'T1serv', 'Product xxx', null, true);
