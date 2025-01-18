CREATE TABLE T_FILE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  deleted_by_fk           BIGINT,

  workflow_instance_id    VARCHAR(36),
  form_id                 VARCHAR(36),
  name                    VARCHAR(100) NOT NULL,
  content_type            VARCHAR(100) NOT NULL,
  content_length          LONG NOT NULL,
  url                     TEXT NOT NULL,
  deleted                 BOOL NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_FILE_WORKFLOW ON T_FILE(workflow_instance_id);
CREATE INDEX I_FILE_FORM ON T_FILE(form_id);


CREATE TABLE T_FILE_OWNER(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  file_fk               BIGINT NOT NULL REFERENCES T_FILE(id),
  owner_fk              BIGINT NOT NULL,

  owner_type            INT NOT NULL DEFAULT 0,

  UNIQUE(file_fk, owner_fk, owner_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, name, title, home_url, tab_url, settings_url)
    VALUES (30, 'file', 'Files', null, '/files/tab', null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (300, 30, 'file',       'Manage files'),
           (301, 30, 'file:admin', 'Configure files');

