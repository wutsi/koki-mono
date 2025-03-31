CREATE TABLE T_TRANSACTION(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,
  invoice_fk              BIGINT,
  created_by_fk           BIGINT,

  type                    INT NOT NULL DEFAULT 0,
  status                  INT NOT NULL DEFAULT 0,
  payment_method_type     INT NOT NULL DEFAULT 0,
  gateway                 INT NOT NULL DEFAULT 0,
  amount                  DECIMAL(10, 2) NOT NULL,
  currency                VARCHAR(3) NOT NULL,
  checkout_url            TEXT,
  supplier_transaction_id VARCHAR(255),
  supplier_status         VARCHAR(30),
  error_code              VARCHAR(255),
  supplier_error_code     VARCHAR(255),
  supplier_error_message  TEXT,
  description             TEXT,

  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_TRANSACTION_tenant ON T_TRANSACTION(tenant_fk);
CREATE INDEX I_TRANSACTION_invoice ON T_TRANSACTION(invoice_fk);
CREATE INDEX I_TRANSACTION_type ON T_TRANSACTION(type);
CREATE INDEX I_TRANSACTION_status ON T_TRANSACTION(status);
CREATE INDEX I_TRANSACTION_created_at ON T_TRANSACTION(created_by_fk);



CREATE TABLE T_PAYMENT_METHOD_CASH(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,
  transaction_fk          VARCHAR(36) NOT NULL REFERENCES T_TRANSACTION(id),
  collected_by_fk         BIGINT,

  collected_at            DATE,

  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_PAYMENT_METHOD_INTERAC(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,
  transaction_fk          VARCHAR(36) NOT NULL REFERENCES T_TRANSACTION(id),

  reference_number        VARCHAR(30) NOT NULL,
  bank_name               VARCHAR(100) NOT NULL,
  sent_at                 DATE,
  cleared_at              DATE,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_PAYMENT_METHOD_CHECK(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,
  transaction_fk          VARCHAR(36) NOT NULL REFERENCES T_TRANSACTION(id),

  check_number            VARCHAR(30) NOT NULL,
  bank_name               VARCHAR(100) NOT NULL,
  check_date              DATE,
  cleared_at              DATE,

  PRIMARY KEY(id)
) ENGINE = InnoDB;


INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (200, 10, 'payment', 'Payments', '/payments', '/payments/tab', '/settings/payments', '/js/payments.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2000, 200, 'payment',        'View payments'),
           (2001, 200, 'payment:manage', 'Add/Edit payment'),
           (2002, 200, 'payment:admin',  'Configure payment module');

