ALTER TABLE T_FILE ADD COLUMN source_url TEXT;
ALTER TABLE T_FILE ADD COLUMN source_url_hash VARCHAR(32);

CREATE INDEX idx_file_source_url_hash ON T_FILE(source_url_hash);
