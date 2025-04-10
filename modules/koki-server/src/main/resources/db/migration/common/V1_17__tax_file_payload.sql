CREATE TABLE T_TAX_FILE(
    file_fk         BIGINT NOT NULL,
    tax_fk          BIGINT NOT NULL,
    tenant_fk       BIGINT NOT NULL,
    data            TEXT,
    created_at      DATETIME DEFAULT NOW(),
    modified_at     DATETIME DEFAULT NOW(),

    PRIMARY KEY (file_fk)
) ENGINE = InnoDB;
