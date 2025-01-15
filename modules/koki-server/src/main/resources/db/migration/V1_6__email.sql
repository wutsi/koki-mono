CREATE TABLE T_EMAIL(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,
  sender_fk               BIGINT NOT NULL,
  recipient_fk            BIGINT NOT NULL,
  created_by_fk           BIGINT,

  subject                 VARCHAR(255) NOT NULL ,
  body                    TEXT NOT NULL,
  recipient_type          INT DEFAULT 0,
  created_at              DATETIME DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_EMAIL_OWNER(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  email_fk              VARCHAR(36) NOT NULL REFERENCES T_EMAIL(id),
  owner_fk              BIGINT NOT NULL,

  owner_type            INT NOT NULL DEFAULT 0,

  UNIQUE(email_fk, owner_fk, owner_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
