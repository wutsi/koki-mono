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
  portal_url              VARCHAR(255) NOT NULL,
  client_portal_url       VARCHAR(255) NOT NULL,
  website_url             TEXT,
  created_at              DATETIME DEFAULT NOW(),

  UNIQUE(name),
  UNIQUE(domain_name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_TENANT_MODULE(
  tenant_fk             BIGINT NOT NULL REFERENCES T_TENANT(id),
  module_fk             BIGINT NOT NULL REFERENCES T_MODULE(id),

  PRIMARY KEY (tenant_fk, module_fk)
) ENGINE = InnoDB;

CREATE TABLE T_TYPE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,

  object_type             INT NOT NULL,
  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(100),
  description             TEXT,
  active                  BOOL NOT NULL DEFAULT true,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, object_type, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_USER(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  category_fk             BIGINT,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  city_fk                 BIGINT,

  username                VARCHAR(50) NOT NULL,
  password                VARCHAR(32) NOT NULL,
  salt                    VARCHAR(36) NOT NULL DEFAULT '',
  email                   VARCHAR(255),
  display_name            VARCHAR(50),
  mobile                  VARCHAR(30),
  language                VARCHAR(2),
  country                 VARCHAR(2),
  employer                VARCHAR(50),
  status                  INT NOT NULL DEFAULT 0,
  photo_url               TEXT,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now(),

  UNIQUE (tenant_fk, username),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_USER_tenant ON T_USER(tenant_fk);
CREATE INDEX I_USER_category ON T_USER(category_fk);
CREATE INDEX I_USER_city ON T_USER(city_fk);


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

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255),
  description             TEXT,
  active                  BOOL NOT NULL DEFAULT true,
  deleted                 BOOL NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_USER_ROLE(
  user_fk       BIGINT NOT NULL REFERENCES T_USER(id),
  role_fk       BIGINT NOT NULL REFERENCES T_ROLE(id),

  PRIMARY KEY(user_fk, role_fk)
) ENGINE = InnoDB;


CREATE TABLE T_ROLE_PERMISSION(
  role_fk       BIGINT NOT NULL REFERENCES T_ROLE(id),
  permission_fk BIGINT NOT NULL REFERENCES T_PERMISSION(id),

  PRIMARY KEY(role_fk, permission_fk)
) ENGINE = InnoDB;


CREATE TABLE T_BUSINESS(
    id                      BIGINT NOT NULL AUTO_INCREMENT,

    tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),
    created_by_fk           BIGINT,
    modified_by_fk          BIGINT,

    company_name            VARCHAR(100) NOT NULL,
    phone                   VARCHAR(30),
    fax                     VARCHAR(30),
    email                   VARCHAR(255),
    website                 TEXT,
    address_street          TEXT,
    address_postal_code     VARCHAR(30),
    address_city_fk         BIGINT,
    address_state_fk        BIGINT,
    address_country         VARCHAR(2),

    created_at              DATETIME DEFAULT NOW(),
    modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

    PRIMARY KEY (id)
) ENGINE = InnoDB;


CREATE TABLE T_BUSINESS_JURIDICTION(
    business_fk         BIGINT NOT NULL REFERENCES T_BUSINESS(id),
    juridiction_fk      BIGINT NOT NULL REFERENCES T_JURIDICTION(id),

    PRIMARY KEY (business_fk, juridiction_fk)
) ENGINE = InnoDB;


INSERT INTO T_MODULE(id, name, title, home_url, tab_url, settings_url)
    VALUES (900, 'security', 'Security', null, null, '/settings/security'),
           (901, 'tenant',   'Tenant',   null, null, '/settings/tenant');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (9000, 901, 'security:admin', 'Manage system security'),
           (9010, 900, 'tenant:admin',   'Manage tenant');
