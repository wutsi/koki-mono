CREATE TABLE T_AGENT(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  user_fk                   BIGINT,

  total_sales               BIGINT,
  total_rentals             BIGINT,
  total_transactions        BIGINT,
  past_12m_sales            BIGINT,
  past_12m_rentals          BIGINT,
  past_12m_transactions     BIGINT,
  created_at                DATETIME NOT NULL DEFAULT NOW(),
  modified_at               DATETIME NOT NULL DEFAULT NOW(),
  last_sold_at              DATE,

  UNIQUE(user_fk),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_AGENT(tenant_fk);
CREATE INDEX last_sold_at ON T_AGENT(last_sold_at);
CREATE INDEX past_12m_transactions ON T_AGENT(past_12m_transactions);

CREATE TABLE T_AGENT_METRIC(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  agent_fk                  BIGINT NOT NULL REFERENCES T_AGENT(id),

  listing_type              INT NOT NULL DEFAULT 0,
  `period`                  INT NOT NULL DEFAULT 0,
  total                     BIGINT NOT NULL DEFAULT 0,
  min_price                 BIGINT NOT NULL DEFAULT 0,
  max_price                 BIGINT NOT NULL DEFAULT 0,
  average_price             BIGINT NOT NULL DEFAULT 0,
  total_price               BIGINT NOT NULL DEFAULT 0,
  currency                  VARCHAR(3),
  created_at                DATETIME NOT NULL DEFAULT NOW(),
  modified_at               DATETIME NOT NULL DEFAULT NOW(),

  UNIQUE(agent_fk, listing_type, period),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (290, 17, 'agent', 'Agents', '/agents', null, null, null, '/css/agents.css');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2900, 290, 'agent',             'View Agents'),
           (2901, 290, 'agent:full_access', 'Full access on all Agents');
