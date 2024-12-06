CREATE TABLE T_FORM(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255) NOT NULL,
  content                 JSON NOT NULL,
  active                  BOOLEAN NOT NULL DEFAULT true,
  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_FORM_DATA(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,
  form_fk                 VARCHAR(36) NOT NULL REFERENCES T_FORM(id),

  workflow_instance_id    VARCHAR(36),
  status                  INT NOT NULL DEFAULT 0,
  data                    JSON NOT NULL,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_FORM_DATA_WORKFLOW ON T_FORM_DATA(workflow_instance_id);
