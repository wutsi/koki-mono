CREATE TABLE T_PRODUCT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  type                    INT NOT NULL DEFAULT 0,
  name                    VARCHAR(100) NOT NULL,
  code                    VARCHAR(30),
  description             TEXT,
  active                  BOOLEAN NOT NULL DEFAULT true,

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_PRICE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  product_fk              BIGINT NOT NULL REFERENCES T_PRODUCT(id),
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  name                    VARCHAR(100),
  amount                  DOUBLE NOT NULL,
  currency                VARCHAR(3) NOT NULL,
  active                  BOOLEAN NOT NULL DEFAULT true,

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
);

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url)
    VALUES (180, 8, 'product', 'Product', '/products', '/products/tab', null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1800, 180, 'product',        'View products'),
           (1801, 180, 'product:manage', 'Add/Edit products'),
           (1802, 180, 'product:delete', 'Delete products');

