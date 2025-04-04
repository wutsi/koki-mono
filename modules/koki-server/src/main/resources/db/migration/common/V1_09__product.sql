CREATE TABLE T_PRODUCT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,
  category_fk             BIGINT,

  type                    INT NOT NULL DEFAULT 0,
  name                    VARCHAR(100) NOT NULL,
  code                    VARCHAR(30),
  description             TEXT,
  active                  BOOLEAN NOT NULL DEFAULT true,

  unit_fk                 BIGINT,
  quantity                INT,

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_PRODUCT_tenant ON T_PRODUCT(tenant_fk);
CREATE INDEX I_PRODUCT_type ON T_PRODUCT(type);
CREATE INDEX I_PRODUCT_active ON T_PRODUCT(active);
CREATE INDEX I_PRODUCT_category ON T_PRODUCT(category_fk);

CREATE TABLE T_PRICE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  product_fk              BIGINT NOT NULL REFERENCES T_PRODUCT(id),
  account_type_fk         BIGINT,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  name                    VARCHAR(100),
  amount                  DECIMAL(10, 2) NOT NULL,
  currency                VARCHAR(3) NOT NULL,
  active                  BOOLEAN NOT NULL DEFAULT true,
  start_at                DATE,
  end_at                  DATE,

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
);

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (160, 6, 'product', 'Products', '/products', '/products/tab', null, '/js/products.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1600, 160, 'product',        'View products'),
           (1601, 160, 'product:manage', 'Add/Edit products'),
           (1602, 160, 'product:delete', 'Delete products');
