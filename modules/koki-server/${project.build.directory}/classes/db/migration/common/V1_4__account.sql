CREATE TABLE T_ATTRIBUTE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  label                   VARCHAR(100),
  description             TEXT,
  choices                 TEXT,
  type                    INT NOT NULL DEFAULT 0,
  required                BOOL NOT NULL DEFAULT false,
  active                  BOOL NOT NULL DEFAULT true,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ACCOUNT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  account_type_fk         BIGINT,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,
  managed_by_fk           BIGINT,

  name                    VARCHAR(100) NOT NULL,
  phone                   VARCHAR(30),
  mobile                  VARCHAR(30),
  email                   VARCHAR(255) NOT NULL,
  website                 TEXT,
  language                VARCHAR(2),
  description             TEXT,

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
  billing_same_as_shipping_address BOOLEAN NOT NULL DEFAULT false,

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  UNIQUE(tenant_fk, email),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_ACCOUNT_tenant ON T_ACCOUNT(tenant_fk);
CREATE INDEX I_ACCOUNT_account_type ON T_ACCOUNT(account_type_fk);

CREATE TABLE T_ACCOUNT_ATTRIBUTE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  attribute_fk            BIGINT NOT NULL REFERENCES T_ATTRIBUTE(id),
  account_fk              BIGINT NOT NULL REFERENCES T_ACCOUNT(id),

  value                   TEXT,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (110, 1, 'account', 'Accounts', '/accounts', null, '/settings/accounts', '/js/accounts.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1100, 110, 'account',        'View accounts profile'),
           (1101, 110, 'account:manage', 'Add/Edit accounts profile'),
           (1102, 110, 'account:delete', 'Delete accounts profile'),
           (1103, 110, 'account:admin',  'Configure account module'),
           (1104, 110, 'account:full_access', 'Access to all accounts');

