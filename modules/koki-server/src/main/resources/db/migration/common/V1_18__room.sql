CREATE TABLE T_ROOM(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  account_fk                BIGINT NOT NULL,
  created_by_fk             BIGINT,
  modified_by_fk            BIGINT,
  deleted_by_fk             BIGINT,
  published_by_fk           BIGINT,
  hero_image_fk             BIGINT,
  category_fk               BIGINT,

  type                      INT NOT NULL DEFAULT 0,
  status                    INT NOT NULL DEFAULT 0,
  deleted                   BOOLEAN NOT NULL DEFAULT false,
  title                     VARCHAR(100),
  summary                   VARCHAR(255),
  description               TEXT,
  number_of_rooms           INT NOT NULL DEFAULT 0,
  number_of_bathrooms       INT NOT NULL DEFAULT 0,
  number_of_beds            INT NOT NULL DEFAULT 0,
  max_guests                INT NOT NULL DEFAULT 0,
  area                      INT NOT NULL DEFAULT 0,
  price_per_night           DECIMAL(10, 2),
  price_per_month           DECIMAL(10, 2),
  currency                  VARCHAR(3),
  checkin_time              VARCHAR(5),
  checkout_time             VARCHAR(5),
  lease_type                INT NOT NULL DEFAULT 0,
  lease_term                INT NOT NULL DEFAULT 0,
  furnished_type            INT NOT NULL DEFAULT 0,

  street                    TEXT,
  postal_code               VARCHAR(30),
  city_fk                   BIGINT,
  state_fk                  BIGINT,
  neighborhood_fk           BIGINT,
  country                   VARCHAR(2),
  latitude                  DOUBLE,
  longitude                 DOUBLE,
  hero_image_reason         TEXT,

  created_at                DATETIME DEFAULT NOW(),
  modified_at               DATETIME DEFAULT NOW(),
  deleted_at                DATETIME,
  published_at              DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_ROOM(tenant_fk);
CREATE INDEX `status` ON T_ROOM(status);
CREATE INDEX `type` ON T_ROOM(type);
CREATE INDEX city ON T_ROOM(city_fk);
CREATE INDEX account ON T_ROOM(account_fk);
CREATE INDEX neighborhood ON T_ROOM(neighborhood_fk);
CREATE INDEX number_of_rooms ON T_ROOM(number_of_rooms);
CREATE INDEX number_of_bathrooms ON T_ROOM(number_of_bathrooms);


CREATE TABLE T_ROOM_AMENITY(
  room_fk                   BIGINT NOT NULL REFERENCES T_ROOM(id),
  amenity_fk                BIGINT,

  created_at                DATETIME DEFAULT NOW(),

  PRIMARY KEY(room_fk, amenity_fk)
) ENGINE = InnoDB;

CREATE TABLE T_ROOM_UNIT(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  room_fk                   BIGINT NOT NULL REFERENCES T_ROOM(id),
  tenant_fk                 BIGINT NOT NULL,
  created_by_fk             BIGINT,
  modified_by_fk            BIGINT,
  deleted_by_fk             BIGINT,

  number                    VARCHAR(10) NOT NULL ,
  floor                     INT NOT NULL DEFAULT 0,
  status                    INT NOT NULL DEFAULT 0,
  deleted                   BOOL DEFAULT false,

  created_at                DATETIME DEFAULT NOW(),
  modified_at               DATETIME DEFAULT NOW(),
  deleted_at                DATETIME,

  UNIQUE(room_fk, number),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (250, 12, 'room',         'Rooms',    '/rooms',  '/rooms/tab',          null, '/js/rooms.js', '/css/rooms.css'),
           (251, 13, 'room-unit',    'Units',     null,     '/room-units/tab',     null, null,           null),
           (252, 0,  'room-amenity', 'Amenities', null,     '/room-amenities/tab', null, null,           null);


INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2500, 250, 'room',        'View rooms'),
           (2501, 250, 'room:manage', 'Add/Edit rooms'),

           (2510, 251, 'room-unit',        'View room units'),
           (2511, 251, 'room-unit:manage', 'Add/Edit room unis'),

           (2520, 252, 'room-amenity',        'View room amenities'),
           (2521, 252, 'room-amenity:manage', 'Add/Edit room amenities');
