PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for asset_info
-- ----------------------------
DROP TABLE IF EXISTS "main"."asset_info";
CREATE TABLE asset_info (name varchar(255) not null, ref_name varchar(255), refurl varchar(255), url varchar(255), primary key (name));

-- ----------------------------
-- Records of asset_info
-- ----------------------------
