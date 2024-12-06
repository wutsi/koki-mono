CREATE TABLE T_MESSAGE(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  subject                 VARCHAR(255) NOT NULL,
  body                    TEXT NOT NULL,
  active                  BOOLEAN NOT NULL DEFAULT true,
  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
