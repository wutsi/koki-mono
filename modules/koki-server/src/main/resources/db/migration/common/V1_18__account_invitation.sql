DROP TABLE T_INVITATION;
CREATE TABLE T_INVITATION(
  id                VARCHAR(36) NOT NULL,

  tenant_fk         BIGINT NOT NULL,
  account_fk        BIGINT NOT NULL REFERENCES T_ACCOUNT(id),
  created_by_fk     BIGINT,

  created_at        DATETIME DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;
