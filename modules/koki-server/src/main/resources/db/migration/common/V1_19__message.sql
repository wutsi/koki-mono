CREATE TABLE T_MESSAGE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  owner_fk                BIGINT,

  owner_type              INT,
  sender_name             VARCHAR(100) NOT NULL,
  sender_email            VARCHAR(255) NOT NULL,
  sender_phone            VARCHAR(30),
  status                  INT NOT NULL DEFAULT 0,
  body                    TEXT NOT NULL,
  created_at              DATETIME DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_MESSAGE(tenant_fk);
CREATE INDEX created ON T_MESSAGE(created_at);
CREATE INDEX owner ON T_MESSAGE(owner_fk, owner_type);

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (260, 14, 'message', 'Messages', null, '/messages/tab', null, '/js/messages.js', '/css/messages.css');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2600, 260, 'message', 'Read messages'),
           (2601, 260, 'message:manage', 'Manage messages');
