CREATE TABLE T_LEAD(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  listing_fk                BIGINT,
  agent_user_fk             BIGINT NOT NULL,
  user_fk                   BIGINT NOT NULL,
  last_message_fk           BIGINT,

  device_id                 VARCHAR(36),
  status                    INT NOT NULL DEFAULT 0,
  source                    INT NOT NULL DEFAULT 0,
  next_contact_at           DATETIME,
  next_visit_at             DATETIME,
  total_messages            INT,

  created_at                DATETIME NOT NULL DEFAULT NOW(),
  modified_at               DATETIME NOT NULL DEFAULT NOW(),

  UNIQUE(listing_fk, user_fk),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_LEAD(tenant_fk);
CREATE INDEX listing ON T_LEAD(listing_fk);
CREATE INDEX `user` ON T_LEAD(user_fk);
CREATE INDEX agent_user ON T_LEAD (agent_user_fk);
CREATE INDEX next_contact_at ON T_LEAD(next_contact_at);
CREATE INDEX next_visit_at ON T_LEAD(next_visit_at);
CREATE INDEX `status` ON T_LEAD(status);

CREATE TABLE T_LEAD_MESSAGE(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  lead_fk                   BIGINT NOT NULL REFERENCES T_LEAD(id),

  content                   TEXT NOT NULL,
  visit_requested_at        DATETIME,
  created_at                DATETIME NOT NULL DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (300, 18, 'lead', 'Leads', '/leads', '/leads/tab', null, null, '/css/leads.css'),
           (301, null, 'lead_message', 'Messages', null, '/leads/messages/tab', null, null, null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (3000, 300, 'lead',             'View Leads'),
           (3001, 300, 'lead:full_access', 'Full access on Leads');
