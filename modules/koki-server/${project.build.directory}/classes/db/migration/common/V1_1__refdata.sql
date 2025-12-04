CREATE TABLE T_LOCATION(
  id          BIGINT NOT NULL,

  parent_fk   BIGINT,

  name        VARCHAR(200) NOT NULL,
  ascii_name  VARCHAR(200) NOT NULL,
  type        INT NOT NULL DEFAULT 0,
  country     VARCHAR(2) NOT NULL,
  population  BIGINT,
  latitude    DOUBLE,
  longitude   DOUBLE,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX T_LOCATION_parent ON T_LOCATION(parent_fk);
CREATE INDEX I_LOCATION_country ON T_LOCATION(country);
CREATE INDEX I_LOCATION_type ON T_LOCATION(type);

CREATE TABLE T_CATEGORY(
  id             BIGINT NOT NULL,

  name           VARCHAR(255) NOT NULL ,
  long_name      TEXT NOT NULL,
  name_fr        TEXT,
  long_name_fr   TEXT,
  type           INT NOT NULL DEFAULT 0,
  level          INT NOT NULL DEFAULT 0,
  active         BOOLEAN NOT NULL DEFAULT true,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

ALTER TABLE T_CATEGORY ADD COLUMN parent_fk BIGINT REFERENCES T_CATEGORY(id);
CREATE INDEX I_CATEGORY_type ON T_CATEGORY(type);



CREATE TABLE T_AMENITY(
  id             BIGINT NOT NULL,

  category_fk    BIGINT NOT NULL REFERENCES T_CATEGORY(id),
  name           TEXT NOT NULL,
  name_fr        TEXT,
  icon           TEXT,
  active         BOOLEAN NOT NULL DEFAULT true,
  top            BOOLEAN NOT NULL DEFAULT false,

  PRIMARY KEY(id)
) ENGINE = InnoDB;
