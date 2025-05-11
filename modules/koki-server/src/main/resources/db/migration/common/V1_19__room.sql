CREATE TABLE T_ROOM(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  created_by_fk             BIGINT,
  modified_by_fk            BIGINT,
  deleted_by_fk             BIGINT,

  type                      INT NOT NULL DEFAULT 0,
  status                    INT NOT NULL DEFAULT 0,
  deleted                   BOOLEAN NOT NULL DEFAULT false,
  title                     VARCHAR(100) NOT NULL,
  description               TEXT,
  number_of_rooms           INT NOT NULL DEFAULT 0,
  number_of_bathrooms       INT NOT NULL DEFAULT 0,
  number_of_beds            INT NOT NULL DEFAULT 0,
  max_guests                INT NOT NULL DEFAULT 0,
  price_per_night           DECIMAL(10, 2),
  currency                  VARCHAR(3),

  street                    TEXT,
  postal_code               VARCHAR(30),
  city_fk                   BIGINT NOT NULL,
  state_fk                  BIGINT,
  country                   VARCHAR(2) NOT NULL,
  latitude                  DOUBLE,
  longitude                 DOUBLE,

  created_at                DATETIME DEFAULT NOW(),
  modified_at               DATETIME DEFAULT NOW(),
  deleted_at                DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_ROOM(tenant_fk);
CREATE INDEX `status` ON T_ROOM(status);
CREATE INDEX city ON T_ROOM(city_fk);


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
    VALUES (250, 12, 'room',         'Rooms',    '/rooms', null,                   null, '/js/rooms.js', '/css/rooms.css'),
           (251, 14, 'room-unit',    'Units',     null,     '/room-units/tab',     null, null,           null),
           (252, 0,  'room-amenity', 'Amenities', null,     '/room-amenities/tab', null, null,           null);


INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2500, 250, 'room',        'View rooms'),
           (2501, 250, 'room:manage', 'Add/Edit rooms'),

           (2510, 251, 'room-unit',        'View room units'),
           (2511, 251, 'room-unit:manage', 'Add/Edit room unis'),

           (2520, 252, 'room-amenity',        'View room amenities'),
           (2521, 252, 'room-amenity:manage', 'Add/Edit room amenities');
