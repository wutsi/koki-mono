CREATE TABLE T_FORM(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  title                   VARCHAR(255),
  description             TEXT,
  active                  BOOLEAN NOT NULL DEFAULT true,
  content                 TEXT NOT NULL,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;