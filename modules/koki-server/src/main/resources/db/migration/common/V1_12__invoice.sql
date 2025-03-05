CREATE TABLE T_INVOICE_SEQUENCE(
  id                BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk         BIGINT NOT NULL,
  current           BIGINT,

  UNIQUE(tenant_fk),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_INVOICE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  customer_account_fk     BIGINT,
  order_fk                BIGINT,
  tax_fk                  BIGINT,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  pdf_opened_file_fk      BIGINT,
  pdf_paid_file_fk        BIGINT,
  pdf_voided_file_fk      BIGINT,

  customer_name           VARCHAR(255) NOT NULL,
  customer_email          VARCHAR(255) NOT NULL,
  customer_phone          VARCHAR(30),
  customer_mobile         VARCHAR(30),

  number                  BIGINT,
  status                  INT NOT NULL DEFAULT 0,
  description             TEXT,
  sub_total_amount        DECIMAL(10, 2) NOT NULL DEFAULT 0,
  total_tax_amount        DECIMAL(10, 2) NOT NULL DEFAULT 0,
  total_discount_amount   DECIMAL(10, 2) NOT NULL DEFAULT 0,
  total_amount            DECIMAL(10, 2) NOT NULL DEFAULT 0,
  amount_paid             DECIMAL(10, 2) NOT NULL DEFAULT 0,
  amount_due              DECIMAL(10, 2) NOT NULL DEFAULT 0,
  currency                VARCHAR(3) NOT NULL,

  shipping_street         TEXT,
  shipping_postal_code    VARCHAR(30),
  shipping_city_fk        BIGINT,
  shipping_state_fk       BIGINT,
  shipping_country        VARCHAR(2),
  billing_street          TEXT,
  billing_postal_code     VARCHAR(30),
  billing_city_fk         BIGINT,
  billing_state_fk        BIGINT,
  billing_country         VARCHAR(2),

  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  invoiced_at             DATE,
  due_at                  DATE,
  due_days                INT,

  UNIQUE(tenant_fk, number),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_INVOICE_tenant ON T_INVOICE(tenant_fk);
CREATE INDEX I_INVOICE_status ON T_INVOICE(status);
CREATE INDEX I_INVOICE_account ON T_INVOICE(customer_account_fk);
CREATE INDEX I_INVOICE_tax ON T_INVOICE(tax_fk);
CREATE INDEX I_INVOICE_order ON T_INVOICE(order_fk);


CREATE TABLE T_INVOICE_ITEM(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  invoice_fk              BIGINT NOT NULL REFERENCES T_INVOICE(id),
  product_fk              BIGINT NOT NULL,
  unit_price_fk           BIGINT NOT NULL,
  unit_fk                 BIGINT,

  quantity                INT NOT NULL DEFAULT 1,
  unit_price              DECIMAL(10, 2) NOT NULL DEFAULT 0,
  sub_total               DECIMAL(10, 2) NOT NULL DEFAULT 0,
  currency                VARCHAR(3) NOT NULL,
  description             TEXT,

  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_INVOICE_TAX(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  invoice_item_fk         BIGINT NOT NULL REFERENCES T_INVOICE_ITEM(id),
  sales_tax_fk            BIGINT NOT NULL,

  description             TEXT,
  rate                    DECIMAL(10, 4),
  amount                  DECIMAL(10, 2),
  currency                VARCHAR(3) NOT NULL,

  UNIQUE(invoice_item_fk, sales_tax_fk),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_INVOICE_LOG(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  invoice_fk              BIGINT NOT NULL REFERENCES T_INVOICE(id),
  created_by_fk           BIGINT,

  status                  INT NOT NULL DEFAULT 0,
  comment                 TEXT,
  created_at              DATETIME NOT NULL DEFAULT now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;


INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (190, 9, 'invoice', 'Invoice', '/invoices', '/invoices/tab', '/settings/invoices', '/js/invoices.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1900, 190, 'invoice',        'View invoices'),
           (1901, 190, 'invoice:manage', 'Add/Edit invoice'),
           (1902, 190, 'invoice:void',   'Void invoice'),
           (1903, 190, 'invoice:admin',  'Configure invoice module');

