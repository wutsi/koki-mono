CREATE TABLE T_CONTACT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  contact_type_fk         BIGINT,
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

CREATE INDEX I_CONTACT_account ON T_CONTACT(account_fk, deleted, tenant_fk);
CREATE INDEX I_CONTACT_contact_type_fk ON T_CONTACT(contact_type_fk, deleted, tenant_fk);

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (120, 2, 'contact', 'Contact', '/contacts', '/contacts/tab', null, '/js/contacts.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1200, 120, 'contact',        'View contacts profile'),
           (1201, 120, 'contact:manage', 'Add/Edit contacts profile'),
           (1202, 120, 'contact:delete', 'Delete contacts profile');

