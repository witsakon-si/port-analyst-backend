PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for price
-- ----------------------------
DROP TABLE IF EXISTS "main"."price";
CREATE TABLE price (name varchar(255) not null, price numeric(19, 2), success_sync boolean not null, sync_date datetime, primary key (name));

-- ----------------------------
-- Records of price
-- ----------------------------
