CREATE TABLE T_CONTACT_TYPE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  title                   VARCHAR(255),
  description             TEXT,
  active                  BOOL NOT NULL DEFAULT true,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

  UNIQUE(tenant_fk, name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_CONTACT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  contact_type_fk         BIGINT REFERENCES T_CONTACT_TYPE(id),
  account_fk              BIGINT,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,

  salutation              VARCHAR(10),
  first_name              VARCHAR(100) NOT NULL,
  last_name               VARCHAR(100) NOT NULL,
  profession              VARCHAR(100),
  employer                VARCHAR(100),
  gender                  INT NOT NULL DEFAULT 0,
  phone                   VARCHAR(30),
  mobile                  VARCHAR(30),
  email                   VARCHAR(255),
  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (120, 2, 'contact', 'Contact', '/contacts', '/contacts/tab', '/settings/contacts', '/js/contacts.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1200, 120, 'contact',        'Access contacts'),
           (1201, 120, 'contact:manage', 'Manage contacts'),
           (1202, 120, 'contact:delete', 'Delete contacts'),
           (1203, 120, 'contact:admin',  'Configure contacts');

