CREATE TABLE T_TENANT(
  id                      BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  domain_name             VARCHAR(100) NOT NULL,
  locale                  VARCHAR(5) NOT NULL,
  number_format           VARCHAR(20) NOT NULL DEFAULT '#,###,###.00',
  currency                VARCHAR(3) NOT NULL  DEFAULT 'USD',
  currency_symbol         VARCHAR(20) NOT NULL DEFAULT '$',
  monetary_format         VARCHAR(20) NOT NULL DEFAULT '$ #,###,###.00',
  date_format             VARCHAR(20) NOT NULL DEFAULT 'yyyy-MM-dd',
  time_format             VARCHAR(20) NOT NULL DEFAULT 'HH:mm',
  date_time_format        VARCHAR(20) NOT NULL DEFAULT 'yyyy-MM-dd HH:mm',
  status                  INT NOT NULL DEFAULT 0,
  logo_url                TEXT,
  icon_url                TEXT,
  created_at              DATETIME DEFAULT NOW(),

  UNIQUE(name),
  UNIQUE(domain_name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_USER(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  email                   VARCHAR(255) NOT NULL,
  password                VARCHAR(32) NOT NULL,
  salt                    VARCHAR(36) NOT NULL DEFAULT '',
  display_name            VARCHAR(255) NOT NULL,
  status                  INT NOT NULL DEFAULT 0,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE (tenant_fk, email),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ATTRIBUTE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  name                    VARCHAR(100) NOT NULL,
  label                   VARCHAR(100),
  description             TEXT,
  choices                 TEXT,
  type                    INT NOT NULL DEFAULT 0,
  active                  BOOL NOT NULL DEFAULT true,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_CONFIGURATION(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  name                    VARCHAR(255),
  value                   TEXT,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ROLE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255),
  description             TEXT,
  active                  BOOL NOT NULL DEFAULT true,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_USER_ROLE(
  user_fk       BIGINT NOT NULL REFERENCES T_USER(id),
  role_fk       BIGINT NOT NULL REFERENCES T_ROLE(id),

  PRIMARY KEY(user_fk, role_fk)
) ENGINE = InnoDB;
