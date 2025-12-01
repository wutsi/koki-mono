UPDATE T_MODULE set tab_url = '/leads/tab' WHERE id = 300;

ALTER TABLE T_LISTING add COLUMN total_leads INT;
