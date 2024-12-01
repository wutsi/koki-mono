CREATE TABLE T_MESSAGE(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  name                    VARCHAR(100) NOT NULL,
  subject                 VARCHAR(255) NOT NULL,
  body                    TEXT NOT NULL,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
