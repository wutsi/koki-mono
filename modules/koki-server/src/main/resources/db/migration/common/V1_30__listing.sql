CREATE TABLE T_LISTING_SEQUENCE(
  id                BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk         BIGINT NOT NULL,
  current           BIGINT,

  UNIQUE(tenant_fk),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_LISTING(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  created_by_fk             BIGINT,
  modified_by_fk            BIGINT,
  seller_agent_user_fk      BIGINT,
  buyer_agent_user_fk       BIGINT,
  hero_image_fk             BIGINT,

  status                    INT DEFAULT 0,
  listing_number            BIGINT NOT NULL,
  listing_type              INT,
  property_type             INT,
  bedrooms                  INT,
  bathrooms                 INT,
  half_bathrooms            INT,
  floors                    INT,
  basement_type             INT,
  level                     INT,
  unit                      VARCHAR(10),
  parking_type              INT,
  parkings                  INT,
  fence_type                INT,
  lot_area                  INT,
  property_area             INT,
  year                      INT,
  furniture_type            INT,
  city_fk                   BIGINT,
  state_fk                  BIGINT,
  neighbourhood_fk          BIGINT,
  street                    VARCHAR(255),
  postal_code               VARCHAR(30),
  country                   VARCHAR(2),
  latitude                  DOUBLE,
  longitude                 DOUBLE,
  price                     BIGINT,
  visit_fees                BIGINT,
  currency                  VARCHAR(3),
  seller_agent_commission   DECIMAL(5, 2),
  buyer_agent_commission    DECIMAL(5, 2),
  seller_agent_commission_amount   BIGINT,
  buyer_agent_commission_amount    BIGINT,
  security_deposit          BIGINT,
  advance_rent              INT,
  lease_term                INT,
  notice_period             INT,

  seller_name               VARCHAR(50),
  seller_email              VARCHAR(255),
  seller_phone              VARCHAR(30),
  seller_id_number          VARCHAR(36),
  seller_id_type            INT,
  seller_id_country         VARCHAR(2),

  buyer_name                VARCHAR(50),
  buyer_email               VARCHAR(255),
  buyer_phone               VARCHAR(30),
  transaction_date          DATE,
  transaction_price         BIGINT,
  actual_seller_agent_commission_amount   BIGINT,
  actual_buyer_agent_commission_amount    BIGINT,

  agent_remarks             TEXT,
  public_remarks            TEXT,

  title                     TEXT,
  summary                   TEXT,
  description               TEXT,
  title_fr                  TEXT,
  summary_fr                TEXT,
  description_fr            TEXT,

  total_images              BIGINT,
  total_files               BIGINT,

  created_at                DATETIME DEFAULT NOW(),
  modified_at               DATETIME DEFAULT NOW(),
  published_at              DATETIME,
  closed_at                 DATETIME,

  UNIQUE(listing_number),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_LISTING(tenant_fk);
CREATE INDEX `status` ON T_LISTING(status);
CREATE INDEX listing_type ON T_LISTING(listing_type);
CREATE INDEX property_type ON T_LISTING(property_area);
CREATE INDEX city ON T_LISTING(city_fk);
CREATE INDEX neighbourhood ON T_LISTING(neighbourhood_fk);
CREATE INDEX bedrooms ON T_LISTING(bedrooms);
CREATE INDEX bathrooms ON T_LISTING(bathrooms);
CREATE INDEX price ON T_LISTING(price);
CREATE INDEX lot_area ON T_LISTING(lot_area);
CREATE INDEX property_area ON T_LISTING(property_area);
CREATE INDEX published_at ON T_LISTING(published_at);
CREATE INDEX transaction_date ON T_LISTING(transaction_date);
CREATE INDEX seller_agent_user ON T_LISTING(seller_agent_user_fk);
CREATE INDEX buyer_agent_user ON T_LISTING(buyer_agent_user_fk);

CREATE TABLE T_LISTING_AMENITY(
  listing_fk                BIGINT NOT NULL REFERENCES T_LISTING(id),
  amenity_fk                BIGINT,

  created_at                DATETIME DEFAULT NOW(),

  PRIMARY KEY(listing_fk, amenity_fk)
) ENGINE = InnoDB;

CREATE TABLE T_LISTING_STATUS(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  listing_fk                BIGINT NOT NULL REFERENCES T_LISTING(id),
  created_by_fk             BIGINT,

  status                    INT NOT NULL DEFAULT 0,
  comment                   TEXT,
  created_at                DATETIME DEFAULT NOW(),

  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (270, 15, 'listing', 'Listings', '/listings', null, null, '/js/listings.js', '/css/listings.css');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2700, 270, 'listing',             'View Listings'),
           (2701, 270, 'listing:manage',      'Add/Edit Listing'),
           (2702, 270, 'listing:full_access', 'Full access on all Listings');
