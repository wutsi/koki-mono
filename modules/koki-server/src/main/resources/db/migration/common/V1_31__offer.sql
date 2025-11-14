CREATE TABLE T_OFFER(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  owner_fk                  BIGINT,
  created_by_fk             BIGINT,
  buyer_agent_user_fk       BIGINT NOT NULL ,
  buyer_contact_fk          BIGINT NOT NULL ,
  seller_agent_user_fk      BIGINT NOT NULL ,
  version_fk                BIGINT,

  owner_type                INT,
  status                    INT DEFAULT 0,
  total_versions            INT DEFAULT 0,
  created_at                DATETIME DEFAULT NOW(),
  modified_at               DATETIME DEFAULT NOW(),
  accepted_at               DATE,
  rejected_at               DATE,
  closed_at                 DATE,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_OFFER(tenant_fk);
CREATE INDEX owner_id ON T_OFFER(owner_fk);
CREATE INDEX owner_type ON T_OFFER(owner_type);
CREATE INDEX buyer_agent ON T_OFFER(buyer_agent_user_fk);
CREATE INDEX seller_agent ON T_OFFER(seller_agent_user_fk);


CREATE TABLE T_OFFER_VERSION(
    id                        BIGINT NOT NULL AUTO_INCREMENT,

    tenant_fk                 BIGINT NOT NULL,
    offer_fk                  BIGINT NOT NULL REFERENCES T_OFFER(id),
    created_by_fk             BIGINT,
    assignee_user_fk          BIGINT,

    submitting_party          INT NOT NULL DEFAULT 0,
    status                    INT DEFAULT 0,
    price                     BIGINT NOT NULL,
    currency                  VARCHAR(3) NOT NULL,
    contingencies             TEXT,
    created_at                DATETIME DEFAULT NOW(),
    modified_at               DATETIME DEFAULT NOW(),
    expires_at                DATE,
    closing_at                DATE,

    PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_OFFER_VERSION(tenant_fk);


CREATE TABLE T_OFFER_STATUS(
    id                        BIGINT NOT NULL AUTO_INCREMENT,

    tenant_fk                 BIGINT NOT NULL,
    offer_fk                  BIGINT NOT NULL REFERENCES T_OFFER(id),
    version_fk                BIGINT NOT NULL REFERENCES T_OFFER_VERSION(id),
    created_by_fk             BIGINT,

    status                    INT DEFAULT 0,
    comment                   TEXT,
    created_at                DATETIME DEFAULT NOW(),

    PRIMARY KEY(id)
) ENGINE = InnoDB;


INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (280, 16, 'offer', 'Offers', '/offers', '/offers/tab', null, null, '/css/offers.css');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2800, 280, 'offer',             'View Offers'),
           (2801, 280, 'offer:manage',      'Add/Edit Offer'),
           (2802, 280, 'offer:full_access', 'Full access on all offers');
