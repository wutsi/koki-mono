CREATE TABLE T_FILE(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),
  created_by_fk           BIGINT REFERENCES T_USER(id),

  workflow_instance_id    VARCHAR(36),
  form_id                 VARCHAR(36),
  name                    VARCHAR(100) NOT NULL,
  content_type            VARCHAR(255) NOT NULL,
  content_length          LONG NOT NULL,
  url                     TEXT NOT NULL,
  deleted                 BOOL NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_FILE_WORKFLOW ON T_FILE(workflow_instance_id);
CREATE INDEX I_FILE_FORM ON T_FILE(form_id);
