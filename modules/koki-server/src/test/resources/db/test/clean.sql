DELETE FROM T_NOTE_OWNER;
DELETE FROM T_NOTE;

DELETE FROM T_FILE_OWNER;
DELETE FROM T_FILE;

DELETE FROM T_EMAIL_OWNER;
DELETE FROM T_ATTACHMENT;
DELETE FROM T_EMAIL;

DELETE FROM T_TAX_PRODUCT;
DELETE FROM T_TAX;

DELETE FROM T_INVOICE_SEQUENCE;
DELETE FROM T_INVOICE_TAX;
DELETE FROM T_INVOICE_ITEM;
DELETE FROM T_INVOICE;

DELETE FROM T_PRICE;
DELETE FROM T_PRODUCT;

DELETE FROM T_CONTACT;

DELETE FROM T_ACCOUNT_ATTRIBUTE;
DELETE FROM T_ACCOUNT;
DELETE FROM T_ATTRIBUTE;

DELETE FROM T_EMPLOYEE;

DELETE FROM T_USER_ROLE;
DELETE FROM T_ROLE_PERMISSION;
DELETE FROM T_ROLE;
DELETE FROM T_USER;

DELETE FROM T_BUSINESS_JURIDICTION;
DELETE FROM T_BUSINESS;
DELETE FROM T_TYPE;
DELETE FROM T_TENANT_MODULE;
DELETE FROM T_CONFIGURATION;
DELETE FROM T_TENANT;

DELETE FROM T_PERMISSION;
DELETE FROM T_MODULE;

DELETE FROM T_UNIT;
DELETE FROM T_SALES_TAX;
DELETE FROM T_JURIDICTION;
DELETE FROM T_LOCATION;

UPDATE T_CATEGORY set parent_fk=null;
DELETE FROM T_CATEGORY;
