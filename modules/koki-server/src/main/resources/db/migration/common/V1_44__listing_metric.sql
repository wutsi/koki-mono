CREATE TABLE T_LISTING_METRIC(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,

  neighbourhood_fk          BIGINT,
  seller_agent_user_fk      BIGINT,
  city_fk                   BIGINT,

  bedrooms                  INT,
  property_category         INT,
  listing_status            INT,
  listing_type              INT,

  total_listings            INT NOT NULL DEFAULT 0,
  min_price                 BIGINT NOT NULL DEFAULT 0,
  max_price                 BIGINT NOT NULL DEFAULT 0,
  total_price               BIGINT NOT NULL DEFAULT 0,
  average_price             BIGINT NOT NULL DEFAULT 0,
  average_lot_area          INT,
  price_per_square_meter    BIGINT,
  currency                  VARCHAR(3),

  created_at                DATETIME DEFAULT NOW(),
  modified_at               DATETIME DEFAULT NOW(),

  UNIQUE(tenant_fk, neighbourhood_fk, seller_agent_user_fk, city_fk, bedrooms, property_category, listing_status, listing_type),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE INDEX tenant ON T_LISTING_METRIC(tenant_fk);
CREATE INDEX neighbourhood ON T_LISTING_METRIC(neighbourhood_fk);
CREATE INDEX seller_agent_user ON T_LISTING_METRIC(seller_agent_user_fk);
CREATE INDEX city ON T_LISTING_METRIC(city_fk);
CREATE INDEX property_category ON T_LISTING_METRIC(property_category);
CREATE INDEX listing_status ON T_LISTING_METRIC(listing_status);
CREATE INDEX listing_type ON T_LISTING_METRIC(listing_type);
