CREATE TABLE T_KPI_ROOM(
  id                        BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk                 BIGINT NOT NULL,
  room_fk                   BIGINT NOT NULL,

  `period`                  DATE,
  total_impressions         LONG NOT NULL,
  total_clicks              LONG NOT NULL,
  total_views               LONG NOT NULL,
  total_messages            LONG NOT NULL,
  total_visitors            LONG NOT NULL,
  ctr                       DECIMAL(4, 4) NOT NULL,
  cvr                       DECIMAL(4, 4) NOT NULL,

  created_at                DATETIME DEFAULT NOW(),
  modified_at               DATETIME DEFAULT NOW(),

  UNIQUE(tenant_fk, room_fk, period),
  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX tenant ON T_KPI_ROOM(tenant_fk);
CREATE INDEX room ON T_KPI_ROOM(room_fk);
CREATE INDEX period ON T_KPI_ROOM(period);
