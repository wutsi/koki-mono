CREATE TABLE T_WORKFLOW(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  approver_role_fk        BIGINT,

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255),
  description             TEXT,
  active                  BOOLEAN NOT NULL DEFAULT true,
  parameters              TEXT,
  workflow_instance_count BIGINT NOT NULL DEFAULT 0,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ACTIVITY(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  workflow_fk             BIGINT NOT NULL REFERENCES T_WORKFLOW(id),
  role_fk                 BIGINT,
  form_fk                 VARCHAR(36),
  message_fk              VARCHAR(36),
  script_fk               VARCHAR(36),
  service_fk              VARCHAR(36),

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255),
  active                  BOOLEAN NOT NULL DEFAULT true,
  type                    INT NOT NULL DEFAULT 0,
  requires_approval       BOOLEAN NOT NULL DEFAULT false,
  description             TEXT,
  event                   VARCHAR(255),
  path                    VARCHAR(100),
  method                  VARCHAR(10),
  input                   JSON,
  output                  JSON,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(workflow_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_FLOW(
  id                  BIGINT NOT NULL AUTO_INCREMENT,

  workflow_fk         BIGINT NOT NULL REFERENCES T_WORKFLOW(id),
  from_fk             BIGINT NOT NULL REFERENCES T_ACTIVITY(id),
  to_fk               BIGINT NOT NULL REFERENCES T_ACTIVITY(id),

  expression          TEXT DEFAULT null,

  UNIQUE(from_fk, to_fk),
  PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE T_WORKFLOW_INSTANCE(
    id                  VARCHAR(36) NOT NULL,

    tenant_fk           BIGINT NOT NULL,
    workflow_fk         BIGINT NOT NULL REFERENCES T_WORKFLOW(id),
    approver_fk         BIGINT,
    created_by_fk       BIGINT,

    title               VARCHAR(255),
    status              INT NOT NULL DEFAULT 0,
    state               JSON,
    parameters          JSON,
    start_at            DATETIME NOT NULL,
    due_at              DATETIME,
    created_at          DATETIME DEFAULT NOW(),
    modified_at         DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
    started_at          DATETIME,
    done_at             DATETIME,

    PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_WI_PARTICIPANT(
    id                      BIGINT NOT NULL AUTO_INCREMENT,

    workflow_instance_fk    VARCHAR(36) NOT NULL REFERENCES T_WORKFLOW_INSTANCE(id),
    user_fk                 BIGINT NOT NULL,
    role_fk                 BIGINT NOT NULL,

    UNIQUE(workflow_instance_fk, user_fk, role_fk),
    PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_WI_ACTIVITY(
    id                      VARCHAR(36) NOT NULL,

    tenant_fk               BIGINT NOT NULL,
    workflow_instance_fk    VARCHAR(36) NOT NULL REFERENCES T_WORKFLOW_INSTANCE(id),
    activity_fk             BIGINT NOT NULL REFERENCES T_ACTIVITY(id),
    assignee_fk             BIGINT,
    approver_fk             BIGINT,

    status                  INT NOT NULL DEFAULT 0,
    approval                INT NOT NULL DEFAULT 0,
    created_at              DATETIME DEFAULT NOW(),
    approved_at             DATETIME,
    started_at              DATETIME,
    done_at                 DATETIME,

    UNIQUE(workflow_instance_fk, activity_fk),
    PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_WI_APPROVAL(
    id                      BIGINT NOT NULL AUTO_INCREMENT,

    activity_instance_fk    VARCHAR(36) NOT NULL REFERENCES T_WI_ACTIVITY(id),
    approver_fk             BIGINT NOT NULL,

    status                  INT NOT NULL DEFAULT 0,
    comment                 TEXT,
    approved_at             DATETIME DEFAULT NOW(),

    PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_WI_LOG_ENTRY(
    id                      VARCHAR(36) NOT NULL,

    tenant_fk               BIGINT NOT NULL,
    workflow_instance_fk    VARCHAR(36) NOT NULL REFERENCES T_WORKFLOW_INSTANCE(id),
    activity_instance_fk    VARCHAR(36) REFERENCES T_WI_ACTIVITY(id),

    sequence_number         BIGINT NOT NULL DEFAULT 0,
    type                    INT NOT NULL DEFAULT 0,
    message                 TEXT,
    stack_trace             TEXT,
    exception               TEXT,
    metadata                JSON,

    created_at              DATETIME DEFAULT NOW(),

    PRIMARY KEY(id)
)
