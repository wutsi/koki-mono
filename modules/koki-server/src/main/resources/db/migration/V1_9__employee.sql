CREATE TABLE T_EMPLOYEE(
  id                      BIGINT NOT NULL,

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  job_title               VARCHAR(100),
  hourly_wage             DECIMAL(12, 2),
  currency                VARCHAR(3),
  status                  INT DEFAULT 0,
  hired_at                DATE,
  terminated_at           DATE,

  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url)
    VALUES (170, 7, 'employee', 'Employee', '/employeed', null, null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1700, 170, 'employee',        'View employees profile'),
           (1701, 170, 'employee:manage', 'Add/Edit employees profile'),
           (1702, 170, 'employee:delete', 'Delete employees profile');

