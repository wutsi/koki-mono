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

  owner_type            INT NOT NULL DEFAULT 0,

  UNIQUE(note_fk, owner_fk, owner_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, name, title, home_url, tab_url, settings_url)
    VALUES (40, 'note', 'Notes', null, '/notes/tab', null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (400, 40, 'notes',       'Manage notes');

