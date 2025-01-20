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

CREATE TABLE T_ACCOUNT_TYPE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255),
  description             TEXT,
  active                  BOOL NOT NULL DEFAULT true,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ACCOUNT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  account_type_fk         BIGINT REFERENCES T_ACCOUNT_TYPE(id),
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,
  managed_by_fk           BIGINT,

  name                    VARCHAR(100) NOT NULL,
  phone                   VARCHAR(30),
  mobile                  VARCHAR(30),
  email                   VARCHAR(255),
  website                 TEXT,
  language                VARCHAR(2),
  description             TEXT,

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

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
    VALUES (110, 1, 'account', 'Account', '/accounts', null, '/settings/accounts', '/js/accounts.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1100, 110, 'account',       'Manage accounts'),
           (1101, 110, 'account:admin', 'Configure accounts');

