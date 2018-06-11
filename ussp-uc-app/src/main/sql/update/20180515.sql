ALTER TABLE HT_BOA_IN_APP ADD COLUMN isOS INT(30) NOT NULL DEFAULT 1 COMMENT '0是外部系统，1是内部系统'; 


CREATE TABLE `HT_BOA_OUT_CLIENT_DETAILS` (
  `appId` VARCHAR(255) NOT NULL, 
  `appSecret` VARCHAR(255) DEFAULT NULL,
  `appCode` VARCHAR(255) DEFAULT NULL,
  `scope` VARCHAR(255) DEFAULT NULL,
  `grantTypes` VARCHAR(255) DEFAULT NULL,
  `redirectUrl` VARCHAR(255) DEFAULT NULL,
  `refresh_token` VARCHAR(600) DEFAULT NULL,
  `additionalInformation` VARCHAR(4096) DEFAULT NULL,
  `autoApproveScopes` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`appId`)
)

INSERT INTO HT_BOA_OUT_CLIENT_DETAILS(appId,appSecret,appCode,additionalInformation) VALUES('HT_APP_CLS','e09b9b0a58e911e88ce70242ac110003','CLS_APP','鸿特金服APP')
INSERT INTO HT_BOA_OUT_CLIENT_DETAILS(appId,appSecret,appCode,additionalInformation) VALUES('HT_APP_WX','a42b0f29692511e88ce70242ac110003','WX_APP','微信')
