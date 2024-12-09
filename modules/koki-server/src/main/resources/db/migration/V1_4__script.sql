CREATE TABLE T_SCRIPT(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255),
  description             TEXT,
  language                INT NOT NULL DEFAULT 0,
  code                    TEXT NOT NULL,
  parameters              TEXT,
  active                  BOOLEAN NOT NULL DEFAULT true,
  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
