
ALTER TABLE `HT_BOA_IN_BM_ORG`
ADD INDEX `ID` (`ID`, `ORG_CODE`, `PARENT_ORG_CODE`) ;

ALTER TABLE `HT_BOA_IN_USER`
MODIFY COLUMN `DATA_SOURCE`  int(32) NOT NULL DEFAULT 1 COMMENT '数据来源(1：用户权限系统；2：钉钉同步;3:信贷系统)' AFTER `JOB_NUMBER`;
  
ALTER TABLE `HT_BOA_IN_LOGIN`
MODIFY COLUMN `STATUS`  varchar(1) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '0' COMMENT '状态: 0 正常 1初始密码  ' AFTER `FAILED_COUNT`;

ALTER TABLE `HT_BOA_IN_USER`
ADD COLUMN `STATUS`  varchar(1) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '0' COMMENT '状态: 0 正常  1禁用 2离职  4冻结 5锁定' AFTER `IS_ORG_USER`;
 

UPDATE  HT_BOA_IN_LOGIN set STATUS='1' WHERE PASSWORD='$2a$10$52b2WeWW1uqhY.no7Q0V7OSDcYESKYfBf.hZ2FVjBW9m0HJcIdnKy';

/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : boa

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-04-13 18:52:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dd_dept
-- ----------------------------
DROP TABLE IF EXISTS `dd_dept`;
CREATE TABLE `dd_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(64) NOT NULL,
  `dept_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `parent_dept_id` varchar(64) DEFAULT NULL,
  `orders` varchar(20) DEFAULT NULL COMMENT '顺序',
  `level` int(11) DEFAULT NULL,
  `extend1` varchar(255) DEFAULT NULL,
  `extend2` varchar(255) DEFAULT NULL,
  `extend3` varchar(255) DEFAULT NULL,
  `create_datetime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ID` (`id`) USING BTREE,
  KEY `DEPTID` (`dept_id`,`parent_dept_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11251 DEFAULT CHARSET=latin1;

/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : boa

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-04-13 18:52:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dd_dept_operator
-- ----------------------------
DROP TABLE IF EXISTS `dd_dept_operator`;
CREATE TABLE `dd_dept_operator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dept_id` varchar(64) NOT NULL,
  `dept_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `parent_dept_id` varchar(64) DEFAULT NULL,
  `orders` varchar(20) DEFAULT NULL COMMENT '顺序',
  `level` int(11) DEFAULT NULL,
  `extend1` varchar(255) DEFAULT NULL,
  `extend2` varchar(255) DEFAULT NULL,
  `extend3` varchar(255) DEFAULT NULL,
  `create_datetime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `operator_type` int(11) DEFAULT NULL COMMENT '1:钉钉新增，2：钉钉删除，3：钉钉修改',
  `syn_flag` varchar(2) DEFAULT NULL COMMENT '同步成功标识：1同步成功，0 未同步',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ID` (`id`) USING BTREE,
  KEY `DEPTID` (`dept_id`,`parent_dept_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12161 DEFAULT CHARSET=latin1;

/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : boa

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-04-13 18:52:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dd_user
-- ----------------------------
DROP TABLE IF EXISTS `dd_dept_user`;
DROP TABLE IF EXISTS `dd_user`;
CREATE TABLE `dd_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '用户名',
  `dept_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '机构编码',
  `email` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '邮箱',
  `id_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '身份证号码',
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '手机号码',
  `job_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '工号',
  `position` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '岗位名称中文',
  `extend1` varchar(255) DEFAULT NULL,
  `extend2` varchar(255) DEFAULT NULL,
  `extend3` varchar(255) DEFAULT NULL,
  `create_datetime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `ID` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37943 DEFAULT CHARSET=latin1;

/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : boa

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-04-13 18:52:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dd_user_operator
-- ----------------------------
DROP TABLE IF EXISTS `dd_user_operator`;
CREATE TABLE `dd_user_operator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '用户名',
  `dept_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '机构编码',
  `email` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '邮箱',
  `id_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '身份证号码',
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '手机号码',
  `job_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '工号',
  `position` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '岗位名称中文',
  `extend1` varchar(255) DEFAULT NULL,
  `extend2` varchar(255) DEFAULT NULL,
  `extend3` varchar(255) DEFAULT NULL,
  `create_datetime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `operator_type` int(11) DEFAULT NULL COMMENT '1:钉钉新增，2：钉钉删除，3：钉钉修改',
  `syn_flag` varchar(2) DEFAULT NULL COMMENT '同步成功标识：1同步成功，0 未同步',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `ID` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37943 DEFAULT CHARSET=latin1;

