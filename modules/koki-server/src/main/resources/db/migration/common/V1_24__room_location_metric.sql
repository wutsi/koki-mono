CREATE TABLE T_ROOM_LOCATION_METRIC(
    id                          BIGINT NOT NULL AUTO_INCREMENT,

    tenant_fk                   BIGINT NOT NULL,
    location_fk                 BIGINT NOT NULL,

    total_published_rentals     INT NOT NULL DEFAULT 0,
    created_at                  DATETIME NOT NULL DEFAULT now(),
    modified_at                 DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

    UNIQUE(tenant_fk, location_fk),
    PRIMARY KEY (id)
);
