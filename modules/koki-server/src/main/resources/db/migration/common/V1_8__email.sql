CREATE TABLE T_EMAIL(
  id                      VARCHAR(36) NOT NULL,

  tenant_fk               BIGINT NOT NULL,
  sender_fk               BIGINT,
  recipient_fk            BIGINT,
  created_by_fk           BIGINT,

  recipient_email         VARCHAR(255),
  recipient_display_name  VARCHAR(100),
  subject                 VARCHAR(255) NOT NULL ,
  body                    TEXT NOT NULL,
  summary                 VARCHAR(255) NOT NULL DEFAULT '',
  recipient_type          INT DEFAULT 0,
  attachment_count        INT NOT NULL DEFAULT 0,
  created_at              DATETIME DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_EMAIL_tenant ON T_EMAIL(tenant_fk);
CREATE INDEX I_EMAIL_recipient ON T_EMAIL(recipient_fk);
CREATE INDEX I_EMAIL_recipient_email ON T_EMAIL(recipient_email);

CREATE TABLE T_EMAIL_OWNER(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  email_fk              VARCHAR(36) NOT NULL REFERENCES T_EMAIL(id),
  owner_fk              BIGINT NOT NULL,

  owner_type            INT NOT NULL DEFAULT 0,

  UNIQUE(email_fk, owner_fk, owner_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_ATTACHMENT(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  email_fk              VARCHAR(36) NOT NULL REFERENCES T_EMAIL(id),
  file_fk               BIGINT NOT NULL,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (150, 5, 'email', 'Email', null, '/emails/tab', '/settings/email', '/js/emails.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1500, 150, 'email',        'Read emails'),
           (1501, 150, 'email:send',   'Send emails'),
           (1502, 150, 'email:admin',  'Configure email module');
