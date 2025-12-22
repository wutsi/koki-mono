CREATE TABLE T_AI_LISTING(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  listing_fk                BIGINT NOT NULL REFERENCES T_LISTING(id),

  text                      TEXT,
  result                    JSON,

  created_at                DATETIME DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;
