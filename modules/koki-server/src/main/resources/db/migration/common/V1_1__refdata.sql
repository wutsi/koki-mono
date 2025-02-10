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
