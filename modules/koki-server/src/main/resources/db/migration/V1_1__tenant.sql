CREATE TABLE T_TENANT(
  id                      BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  domain_name             VARCHAR(100) NOT NULL,
  locale                  VARCHAR(5) NOT NULL,
  currency                VARCHAR(3) NOT NULL,
  status                  INT NOT NULL DEFAULT 0,
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

  name                    VARCHAR(255) NOT NULL,
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

  attribute_fk            BIGINT NOT NULL REFERENCES T_ATTRIBUTE(id),

  value                   TEXT,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(attribute_fk),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ROLE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  name                    VARCHAR(100) NOT NULL,
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

  created_at    DATETIME DEFAULT NOW(),

  PRIMARY KEY(user_fk, role_fk)
) ENGINE = InnoDB;
