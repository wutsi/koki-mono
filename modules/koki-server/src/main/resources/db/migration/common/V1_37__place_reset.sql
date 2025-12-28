DROP TABLE T_PLACE_RATING;
DROP TABLE T_PLACE;

CREATE TABLE T_PLACE(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  created_by_fk             BIGINT,
  modified_by_fk            BIGINT,
  hero_image_fk             BIGINT,
  neighbourhood_fk          BIGINT NOT NULL,

  name                      VARCHAR(255) NOT NULL,
  ascii_name                VARCHAR(255) NOT NULL,
  type                      INT NOT NULL DEFAULT 0,
  status                    INT NOT NULL DEFAULT 0,
  summary                   TEXT,
  summary_fr                TEXT,
  introduction              TEXT,
  introduction_fr           TEXT,
  description               TEXT,
  description_fr            TEXT,
  longitude                 DOUBLE,
  latitude                  DOUBLE,
  website_url               VARCHAR(255),
  phone_number              VARCHAR(30),

  private                   BOOLEAN,
  international             BOOLEAN,
  diplomas                  VARCHAR(255),
  languages                 VARCHAR(100),
  academic_systems          VARCHAR(100),
  faith                     INT,
  levels                    VARCHAR(100),

  rating                    DECIMAL(3, 2),

  created_at                DATETIME DEFAULT NOW(),
  modified_at               DATETIME DEFAULT NOW(),
  deleted                   BOOLEAN NOT NULL DEFAULT false,
  deleted_at                DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_PLACE(tenant_fk);
CREATE INDEX neighbourhood ON T_PLACE(neighbourhood_fk);
CREATE INDEX type ON T_PLACE(type);
CREATE INDEX status ON T_PLACE(status);
CREATE INDEX deleted ON T_PLACE(deleted);

CREATE TABLE T_PLACE_RATING(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  place_fk                  BIGINT NOT NULL REFERENCES T_PLACE(id),

  criteria                  INT NOT NULL DEFAULT 0,
  value                     INT NOT NULL,
  reason                    TEXT,

  UNIQUE(place_fk, criteria),
  PRIMARY KEY(id)
) ENGINE = InnoDB;
