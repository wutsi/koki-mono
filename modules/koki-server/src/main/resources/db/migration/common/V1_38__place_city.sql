ALTER TABLE T_PLACE ADD COLUMN city_fk BIGINT;
UPDATE T_PLACE set city_fk = (SELECT n.parent_fk FROM T_LOCATION n WHERE n.id = neighbourhood_fk AND n.type=4);

ALTER TABLE T_PLACE MODIFY COLUMN city_fk BIGINT NOT NULL;
CREATE UNIQUE INDEX city_type_name ON T_PLACE(city_fk, type, ascii_name);
