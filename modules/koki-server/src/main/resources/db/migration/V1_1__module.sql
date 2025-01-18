CREATE TABLE T_MODULE(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  name                    VARCHAR(10) NOT NULL,
  title                   VARCHAR(30) NOT NULL,
  description             TEXT,
  home_url                TEXT,
  tab_url                 TEXT,
  settings_url            TEXT,

  UNIQUE(name),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_PERMISSION(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  module_fk               BIGINT NOT NULL,

  name                    VARCHAR(100) NOT NULL,
  description             TEXT,

  PRIMARY KEY(id)
) ENGINE = InnoDB;
