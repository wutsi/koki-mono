INSERT INTO T_INVOICE(
    id, tenant_fk, tax_fk, order_fk,
    number, status, description,
    sub_total_amount, total_tax_amount, total_discount_amount, total_amount, amount_paid, amount_due, currency,
    customer_account_fk, customer_name, customer_email, customer_phone, customer_mobile,
    shipping_street, shipping_postal_code, shipping_city_fk, shipping_state_fk, shipping_country,
    billing_street, billing_postal_code, billing_city_fk, billing_state_fk, billing_country,
    due_at)
  VALUES(
    100, 1, 7777, 9999,
    10955, 2, 'Sample description',
    800.00, 40.00, 20.00, 820.00, 810.00, 10.00, 'CAD',
    111, 'Ray Sponsible', 'ray.sponsible@gmail.com', '+5147580111', '+514758000',
    '340 Pascal', 'H1K1C1', 111, 100, 'CA',
    '311 Pascal', 'H2K2C2', 211, 200, 'CA',
    '2025-01-30');

INSERT INTO T_INVOICE_ITEM(id, invoice_fk, product_fk, unit_price_fk, unit_fk, quantity, unit_price, sub_total, currency, description)
VALUES (110, 100, 1, 11, 3, 2, 300, 600, 'CAD', 'product 1'),
       (120, 100, 2, 22, 5, 1, 200, 200, 'CAD', 'product 2');

INSERT INTO T_INVOICE_TAX(id, invoice_item_fk, sales_tax_fk, rate, amount, currency)
VALUES (111, 110, 20, 5.000, 10.00, 'CAD'),
       (112, 110, 21, 9.975, 25.00, 'CAD'),
       (121, 120, 20, 5.000,  5.00, 'CAD');
