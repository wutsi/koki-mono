CREATE TABLE T_WEBSITE(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk             BIGINT NOT NULL,
  user_fk               BIGINT NOT NULL,

  base_url              VARCHAR(255) NOT NULL,
  base_url_hash         VARCHAR(32) NOT NULL,
  listing_url_prefix    VARCHAR(255) NOT NULL,
  content_selector      VARCHAR(255),
  image_selector        VARCHAR(255),
  home_urls             TEXT,
  active                BOOLEAN DEFAULT TRUE,

  created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  UNIQUE(tenant_fk, base_url_hash),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX idx_website_tenant_user ON T_WEBSITE(tenant_fk, user_fk);
CREATE INDEX idx_website_active ON T_WEBSITE(active);


CREATE TABLE T_WEBPAGE(
  id                    BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk             BIGINT NOT NULL,
  website_fk            BIGINT NOT NULL,

  url                   VARCHAR(2048) NOT NULL,
  url_hash              VARCHAR(32) NOT NULL,
  content               TEXT,
  image_urls            TEXT,
  active                BOOLEAN DEFAULT TRUE,

  created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  UNIQUE(tenant_fk, url_hash),
  PRIMARY KEY(id),
  FOREIGN KEY(website_fk) REFERENCES T_WEBSITE(id)
) ENGINE = InnoDB;

CREATE INDEX idx_webpage_website ON T_WEBPAGE(website_fk);
CREATE INDEX idx_webpage_tenant ON T_WEBPAGE(tenant_fk);
CREATE INDEX idx_webpage_active ON T_WEBPAGE(active);

