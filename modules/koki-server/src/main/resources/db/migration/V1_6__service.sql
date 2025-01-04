CREATE TABLE T_SERVICE(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255),
  description             TEXT,
  base_url                TEXT,
  authorization_type      INT NOT NULL DEFAULT 0,
  username                TEXT,
  password                TEXT,
  api_key                 TEXT,
  active                  BOOLEAN NOT NULL DEFAULT true,
  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
