CREATE TABLE T_TAX_TYPE(
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

CREATE TABLE T_TAX(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  tax_type_fk             BIGINT REFERENCES T_TAX_TYPE(id),
  account_fk              BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,
  accountant_fk           BIGINT,
  technician_fk           BIGINT,
  assignee_fk             BIGINT,

  status                  INT NOT NULL DEFAULT 0,
  fiscal_year             INT,
  description             TEXT,

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,
  start_at                DATETIME,
  due_at                  DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;
