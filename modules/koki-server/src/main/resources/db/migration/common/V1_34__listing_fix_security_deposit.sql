update T_LISTING set security_deposit=security_deposit/price where security_deposit>price AND price is not null;
alter table T_LISTING modify column security_deposit INT;
