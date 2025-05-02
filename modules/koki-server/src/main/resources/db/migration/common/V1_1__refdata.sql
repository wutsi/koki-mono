CREATE TABLE T_UNIT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  name                    VARCHAR(100) NOT NULL,
  abbreviation            VARCHAR(5),

  UNIQUE(name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_UNIT(id, name)
    VALUES (100, 'Each'),
           (110, 'Hour'),
           (111, 'Day'),
           (112, 'Week'),
           (113, 'Month'),
           (120, 'Session'),
           (121, 'Class'),
           (122, 'Consultation'),
           (130, 'Project'),
           (131, 'Website'),
           (132, 'Design'),
           (140, 'Visit'),
           (141, 'Treatment'),
           (142, 'Lesson')
;


CREATE TABLE T_LOCATION(
  id          BIGINT NOT NULL,

  parent_fk   BIGINT,

  name        VARCHAR(200) NOT NULL,
  ascii_name  VARCHAR(200) NOT NULL,
  type        INT NOT NULL DEFAULT 0,
  country     VARCHAR(2) NOT NULL,
  population  BIGINT,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX T_LOCATION_parent ON T_LOCATION(parent_fk);
CREATE INDEX I_LOCATION_country ON T_LOCATION(country);
CREATE INDEX I_LOCATION_type ON T_LOCATION(type);

CREATE TABLE T_JURIDICTION(
  id             BIGINT NOT NULL,

  state_fk       BIGINT REFERENCES T_LOCATION(id),
  country        VARCHAR(2) NOT NULL,

  UNIQUE(country, state_fk),
  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_SALES_TAX(
  id             BIGINT NOT NULL AUTO_INCREMENT,

  juridiction_fk BIGINT REFERENCES T_JURIDICTION(id),

  name           VARCHAR(30),
  rate           DECIMAL(10, 4) NOT NULL,
  active         BOOLEAN NOT NULL DEFAULT true,
  priority       INT NOT NULL DEFAULT 0,

  PRIMARY KEY(id)
) ENGINE = InnoDB;


CREATE TABLE T_CATEGORY(
  id             BIGINT NOT NULL,

  name           VARCHAR(255) NOT NULL ,
  long_name      TEXT NOT NULL ,
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
  icon           TEXT,
  active         BOOLEAN NOT NULL DEFAULT true,

  PRIMARY KEY(id)
) ENGINE = InnoDB;
