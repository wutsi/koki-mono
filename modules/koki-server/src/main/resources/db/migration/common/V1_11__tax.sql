CREATE TABLE T_TAX(
  id                      BIGINT NOT NULL AUTO_INCREMENT,

  tenant_fk               BIGINT NOT NULL,
  tax_type_fk             BIGINT,
  account_fk              BIGINT NOT NULL,
  created_by_fk           BIGINT,
  modified_by_fk          BIGINT,
  deleted_by_fk           BIGINT,
  accountant_fk           BIGINT,
  technician_fk           BIGINT,
  assignee_fk             BIGINT,

  status                  INT NOT NULL DEFAULT 0,
  fiscal_year             INT,
  description             TEXT,

  deleted                 BOOLEAN NOT NULL DEFAULT false,
  created_at              DATETIME DEFAULT NOW(),
  modified_at             DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
  deleted_at              DATETIME,
  start_at                DATETIME,
  due_at                  DATETIME,

  PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE INDEX I_TAX_status ON T_TAX(status, deleted, tenant_fk);
CREATE INDEX I_TAX_type ON T_TAX(tax_type_fk, deleted, tenant_fk);
CREATE INDEX I_TAX_account ON T_TAX(account_fk, deleted, tenant_fk);
CREATE INDEX I_TAX_assigned ON T_TAX(assignee_fk, deleted, tenant_fk);
CREATE INDEX I_TAX_accountant ON T_TAX(accountant_fk, deleted, tenant_fk);
CREATE INDEX I_TAX_technician ON T_TAX(technician_fk, deleted, tenant_fk);

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url)
    VALUES (160, 6, 'tax', 'Taxes', '/taxes', '/taxes/tab', null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1600, 160, 'tax',        'View tax reports'),
           (1601, 160, 'tax:manage', 'Add/Edit tax reports'),
           (1602, 160, 'tax:delete', 'Delete tax reports'),
           (1604, 160, 'tax:status', 'Change tax report status and assignment');

