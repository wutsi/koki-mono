-- Units
CREATE TABLE T_UNIT(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  name                    VARCHAR(100) NOT NULL,
  abbreviation            VARCHAR(5),

  UNIQUE(name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

INSERT INTO T_UNIT(id, name)
    VALUES (110, 'Hour'),
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

-- Locations
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

-- SalesTax
CREATE TABLE T_SALES_TAX(
  id             BIGINT NOT NULL AUTO_INCREMENT,

  state_fk       BIGINT REFERENCES T_LOCATION(id),

  name           VARCHAR(30),
  country        VARCHAR(2) NOT NULL,
  rate           DECIMAL(10, 4) NOT NULL,
  active         BOOLEAN NOT NULL DEFAULT true,
  priority       INT NOT NULL DEFAULT 0,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_SALES_TAX_country ON T_SALES_TAX(country);
