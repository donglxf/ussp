ALTER TABLE `BOA`.`HT_BOA_IN_ORG`
ADD COLUMN `DATA_SOURCE` int(32) NULL DEFAULT 1 COMMENT '数据来源(1：用户权限系统；2：钉钉同步)' AFTER `ORG_TYPE`;

ALTER TABLE `BOA`.`HT_BOA_IN_USER`
  ADD COLUMN `DATA_SOURCE` int(32) NOT NULL DEFAULT 1 COMMENT '数据来源(1：用户权限系统；2：钉钉同步)' AFTER `JOB_NUMBER`,
  ADD COLUMN `USER_TYPE` varchar(2) NULL COMMENT '用户类型' AFTER `DATA_SOURCE`,
  ADD COLUMN `IS_ORG_USER` int(0) NOT NULL DEFAULT 1 COMMENT '是否是组织用户(1：是 0：否)' AFTER `USER_TYPE`;

CREATE TABLE `BOA`.`HT_BOA_IN_CONTRAST`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) NOT NULL COMMENT '对照类型(10：组织机构；20：用户；30：岗位；)',
  `uc_business_id` varchar(64) NOT NULL COMMENT 'UC系统业务ID',
  `dd_business_id` varchar(64) NULL COMMENT '钉钉系统业务ID',
  `bm_business_id` varchar(64) NULL COMMENT '信贷系统业务ID',
  `extend_business_id_1` varchar(64) NULL COMMENT '扩展业务ID1',
  `extend_business_id_2` varchar(64) NULL COMMENT '扩展业务ID2',
  `extend_business_id_3` varchar(64) NULL COMMENT '扩展业务ID3',
  `extend_business_id_4` varchar(64) NULL COMMENT '扩展业务ID4',
  `extend_business_id_5` varchar(64) NULL COMMENT '扩展业务ID5',
  `contrast` varchar(64) NULL COMMENT '对照人',
  `contrast_datetime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '对照时间',
  PRIMARY KEY (`id`)
);