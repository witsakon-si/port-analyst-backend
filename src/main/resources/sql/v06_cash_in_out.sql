PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for cash_in_out
-- ----------------------------
DROP TABLE IF EXISTS "main"."cash_in_out";
CREATE TABLE cash_in_out (id bigint not null, active boolean not null, created_at datetime, updated_at datetime, version integer not null, account varchar(255), amount numeric(19, 2), cash_type varchar(255), transaction_date datetime, primary key (id));

-- ----------------------------
-- Records of cash_in_out
-- ----------------------------
