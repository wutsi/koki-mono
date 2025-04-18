CREATE TABLE T_TAX(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  tax_type_fk             BIGINT,
  account_fk              BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,
  accountant_fk           BIGINT,
  technician_fk           BIGINT,
  assignee_fk             BIGINT,
  invoice_fk              BIGINT,

  status                  INT NOT NULL DEFAULT 0,
  fiscal_year             INT,
  description             TEXT,
  product_count           INT NOT NULL DEFAULT 0,
  total_revenue           DECIMAL(10, 2),
  total_labor_cost        DECIMAL(10, 2),
  total_labor_duration    DECIMAL(10, 2),
  currency                VARCHAR(3),

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,
  start_at                DATETIME,
  due_at                  DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_TAX_tenant ON T_TAX(tenant_fk);
CREATE INDEX I_TAX_status ON T_TAX(status);
CREATE INDEX I_TAX_type ON T_TAX(tax_type_fk);
CREATE INDEX I_TAX_account ON T_TAX(account_fk);
CREATE INDEX I_TAX_assigned ON T_TAX(assignee_fk);
CREATE INDEX I_TAX_accountant ON T_TAX(accountant_fk);
CREATE INDEX I_TAX_technician ON T_TAX(technician_fk);
CREATE INDEX I_TAX_invoice ON T_TAX(invoice_fk);



CREATE TABLE T_TAX_PRODUCT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  tax_fk                  BIGINT NOT NULL REFERENCES T_TAX(id),
  product_fk              BIGINT NOT NULL,
  unit_price_fk           BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,

  quantity                INT NOT NULL DEFAULT 1,
  unit_price              DECIMAL(10, 2) NOT NULL DEFAULT 0,
  sub_total               DECIMAL(10, 2) NOT NULL DEFAULT 0,
  currency                VARCHAR(3) NOT NULL,
  description             TEXT,

  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_TAX_PRODUCT_unit_price ON T_TAX_PRODUCT(unit_price_fk, tenant_fk);
CREATE INDEX I_TAX_PRODUCT_product ON T_TAX_PRODUCT(product_fk, tenant_fk);


INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (180, 8, 'tax', 'Taxes', '/taxes', '/taxes/tab', '/settings/taxes', '/js/taxes.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1800, 180, 'tax',        'View tax reports'),
           (1801, 180, 'tax:manage', 'Add/Edit tax reports'),
           (1802, 180, 'tax:delete', 'Delete tax reports'),
           (1803, 180, 'tax:metric', 'View tax metrics'),
           (1804, 180, 'tax:admin',  'Configure tax module');
