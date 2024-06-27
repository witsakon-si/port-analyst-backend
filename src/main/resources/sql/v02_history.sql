PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for history
-- ----------------------------
DROP TABLE IF EXISTS "main"."history";
CREATE TABLE history (id bigint not null, active boolean not null, created_at datetime, updated_at datetime, version integer not null, amount numeric(19, 2), commission numeric(19, 2), commission_rate numeric(19, 2), fee numeric(19, 2), fee_rate numeric(19, 2), name varchar(255), net_amount numeric(19, 2), transaction_date datetime, unit integer, unit_price numeric(19, 2), vat numeric(19, 2), vat_rate numeric(19, 2), primary key (id));

-- ----------------------------
-- Records of history
-- ----------------------------
