CREATE TABLE T_NOTE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  subject                 VARCHAR(255) NOT NULL ,
  body                    TEXT NOT NULL,
  deleted                 BOOL NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_NOTE_OWNER(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  note_fk               BIGINT NOT NULL REFERENCES T_NOTE(id),
  owner_fk              BIGINT NOT NULL,

  owner_type            VARCHAR(30) NOT NULL,

  UNIQUE(note_fk, owner_fk, owner_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
