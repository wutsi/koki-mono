CREATE TABLE T_FILE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  deleted_by_fk           BIGINT,

  name                    VARCHAR(100) NOT NULL,
  description             TEXT,
  content_type            VARCHAR(255) NOT NULL,
  content_length          LONG NOT NULL,
  language                VARCHAR(2),
  number_of_pages         INT,
  url                     TEXT NOT NULL,
  deleted                 BOOL NOT NULL DEFAULT false,
  created_at              DATETIME NOT NULL DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT NOW(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_FILE_OWNER(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  file_fk               BIGINT NOT NULL REFERENCES T_FILE(id),
  owner_fk              BIGINT NOT NULL,

  owner_type            INT NOT NULL DEFAULT 0,

  UNIQUE(file_fk, owner_fk, owner_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_LABEL(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,

  name                    VARCHAR(255) NOT NULL,
  display_name            VARCHAR(255) NOT NULL,
  created_at              DATETIME NOT NULL DEFAULT NOW(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_FILE_LABEL(
  file_fk              BIGINT NOT NULL REFERENCES T_FILE(id),
  label_fk             BIGINT NOT NULL REFERENCES T_LABEL(id),

  created_at           DATETIME DEFAULT NOW(),

  PRIMARY KEY (file_fk, label_fk)
) ENGINE = InnoDB;


INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (130, 3, 'file', 'Files', null, '/files/tab', '/settings/files', '/js/files.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1300, 130, 'file',         'Download files'),
           (1301, 130, 'file:manage',  'Upload files'),
           (1302, 130, 'file:delete',  'Delete files'),
           (1303, 130, 'file:admin',   'Configure file module');

