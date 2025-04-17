ALTER TABLE T_USER DROP COLUMN type;

CREATE TABLE T_ACCOUNT_USER(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  account_fk              BIGINT NOT NULL REFERENCES T_ACCOUNT(id),

  username                VARCHAR(100) NOT NULL,
  password                VARCHAR(32) NOT NULL,
  salt                    VARCHAR(36) NOT NULL DEFAULT '',
  status                  INT NOT NULL DEFAULT 0,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now(),

  UNIQUE (tenant_fk, username),
  UNIQUE (tenant_fk, account_fk),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
