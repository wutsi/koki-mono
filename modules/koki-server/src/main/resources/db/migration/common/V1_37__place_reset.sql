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


-- Re-Register module
DELETE FROM T_PERMISSION where module_fk=302;
DELETE FROM T_MODULE where id=302;

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (310, 19, 'place', 'Places', '/places', null, null, null, '/css/places.css');

-- Re-Register permissions
INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (3100, 310, 'place:read',        'View Places'),
           (3101, 310, 'place:manage',      'Add/Edit Place'),
           (3102, 310, 'place:delete',      'Delete Place'),
           (3103, 310, 'place:full_access', 'Full access on all Places');
