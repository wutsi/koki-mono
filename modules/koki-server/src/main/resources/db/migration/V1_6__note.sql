CREATE TABLE T_NOTE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  subject                 VARCHAR(255) NOT NULL ,
  summary                 VARCHAR(255) NOT NULL DEFAULT '',
  body                    TEXT NOT NULL,
  type                    INT NOT NULL DEFAULT 0,
  duration                INT NOT NULL DEFAULT 0,

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
  created_at            DATETIME DEFAULT NOW(),

  UNIQUE(note_fk, owner_fk, owner_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (140, 4, 'note', 'Notes', null, '/notes/tab', null, '/js/notes.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1400, 140, 'note',        'Read notes'),
           (1401, 140, 'note:manage', 'Add/Edit notes'),
           (1402, 140, 'note:delete', 'Delete notes');

