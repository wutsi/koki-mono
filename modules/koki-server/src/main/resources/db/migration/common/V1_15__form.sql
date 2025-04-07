CREATE TABLE T_FORM(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  code                    VARCHAR(10) NOT NULL,
  name                    VARCHAR(100) NOT NULL,
  description             TEXT,
  active                  BOOL NOT NULL DEFAULT true,

  deleted                 BOOL NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  UNIQUE(tenant_fk, code),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_FORM_OWNER(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  form_fk               BIGINT NOT NULL REFERENCES T_FORM(id),
  owner_fk              BIGINT NOT NULL,

  owner_type            INT NOT NULL DEFAULT 0,
  created_at            DATETIME DEFAULT NOW(),

  UNIQUE(form_fk, owner_fk, owner_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (220, 11, 'form', 'Forms', '/forms', null, null, null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2200, 220, 'form',        'Read forms'),
           (2201, 220, 'form:manage', 'Add/Edit forms'),
           (2202, 220, 'form:delete', 'Delete forms');

