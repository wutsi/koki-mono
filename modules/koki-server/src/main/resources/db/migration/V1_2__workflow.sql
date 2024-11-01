CREATE TABLE T_WORKFLOW(
  id                      BIGINT NOT NULL,

  tenant_fk               BIGINT NOT NULL REFERENCES T_TENANT(id),

  name                    VARCHAR(255) NOT NULL,
  description             TEXT,
  active                  BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ACTIVITY(
  id                      BIGINT NOT NULL,

  workflow_fk             BIGINT NOT NULL REFERENCES T_WORKFLOW(id),

  code                    VARCHAR(100) NOT NULL,
  name                    VARCHAR(255) NOT NULL,
  active                  BOOLEAN NOT NULL DEFAULT false,
  type                    INT NOT NULL DEFAULT 0,
  requires_approval       BOOLEAN NOT NULL DEFAULT false,
  description             TEXT,
  tags                    TEXT,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(workflow_fk, code),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ACTIVITY_PRECEDENT(
    activity_fk         BIGINT NOT NULL REFERENCES T_ACTIVITY(id),
    precedent_fk        BIGINT NOT NULL REFERENCES T_ACTIVITY(id),

    PRIMARY KEY(activity_fk, precedent_fk)
) ENGINE = InnoDB;
