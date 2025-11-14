CREATE TABLE T_LEAD(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  listing_fk                BIGINT,

  status                    INT NOT NULL DEFAULT 0,
  source                    INT NOT NULL DEFAULT 0,
  first_name                VARCHAR(50) NOT NULL,
  last_name                 VARCHAR(50) NOT NULL,
  email                     VARCHAR(255),
  phone_number              VARCHAR(30) NOT NULL,
  message                   TEXT,
  visit_requested_at        DATETIME,
  next_contact_at           DATETIME,
  next_visit_at             DATETIME,

  created_at                DATETIME NOT NULL DEFAULT NOW(),
  modified_at               DATETIME NOT NULL DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_LEAD(tenant_fk);
CREATE INDEX listing ON T_LEAD(listing_fk);
CREATE INDEX next_contact_at ON T_LEAD(next_contact_at);
CREATE INDEX next_visit_at ON T_LEAD(next_visit_at);
CREATE INDEX status ON T_LEAD(status);
CREATE INDEX first_last_email ON T_LEAD(first_name, last_name, email);


INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (300, 18, 'lead', 'Leads', '/leads', null, null, null, '/css/leads.css');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (3000, 300, 'lead',             'View Leads'),
           (3001, 300, 'lead:full_access', 'Full access on Leads');
