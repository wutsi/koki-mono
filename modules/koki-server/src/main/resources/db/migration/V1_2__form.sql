CREATE TABLE T_FORM(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255) NOT NULL,
  content                 JSON NOT NULL,
  active                  BOOLEAN NOT NULL DEFAULT true,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;