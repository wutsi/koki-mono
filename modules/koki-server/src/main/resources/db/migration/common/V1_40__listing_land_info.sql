-- Add legal information fields to listing table
ALTER TABLE T_LISTING ADD COLUMN land_title BOOLEAN DEFAULT NULL;
ALTER TABLE T_LISTING ADD COLUMN technical_file BOOLEAN DEFAULT NULL;
ALTER TABLE T_LISTING ADD COLUMN number_of_signers INT DEFAULT NULL;
ALTER TABLE T_LISTING ADD COLUMN mutation_type INT DEFAULT NULL;
ALTER TABLE T_LISTING ADD COLUMN transaction_with_notary BOOLEAN DEFAULT NULL;

-- Add index for potential queries filtering by legal information
CREATE INDEX idx_listing_legal ON T_LISTING(land_title, technical_file, mutation_type, transaction_with_notary);

