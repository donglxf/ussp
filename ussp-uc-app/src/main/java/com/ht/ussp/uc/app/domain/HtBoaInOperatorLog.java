package com.ht.ussp.uc.app.domain;

import java.io.Serializable;
import javax.persistence.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 
* @ClassName: HtBoaInOperatorLog
* @Description: 操作日志表
* @author yaojiehong@hongte.info
* @date 2018年1月13日 上午10:02:17
 */
@Entity
@Data
@Table(name = "HT_BOA_IN_OPERATOR_LOG")
@ApiModel(value = "HtBoaInOperatorLog", description = "操作日志")
public class HtBoaInOperatorLog implements Serializable {

    private static final long serialVersionUID = 7919957512241600823L;

    @ApiModelProperty(value = "ID")
    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    

    @ApiModelProperty(value = "用户ID")
    @Column(name="USER_ID")
    private String userId;

    @ApiModelProperty(value = "用户名")
    @Column(name="USER_NAME")
    private String userName;

    @ApiModelProperty(value = "操作名称")
    @Column(name="ACTION_NAME")
    private String actionName;

    @ApiModelProperty(value = "设备类型")
    @Column(name="DEVICE_TYPE")
    private String deviceType;

    @ApiModelProperty(value = "结果")
    @Column(name="RESULT")
    private String result;

    @ApiModelProperty(value = "客户端IP")
    @Column(name="CLIENT_IP")
    private String clientIp;

    @ApiModelProperty(value = "发生时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ACTION_TIME")
    private Date actionTime;

    @ApiModelProperty(value = "设备号")
    @Column(name="DEVICE_NO")
    private String deviceNo;

    @ApiModelProperty(value = "系统编码")
    @Column(name="APP")
    private String app;

    @ApiModelProperty(value = "日志类型")
    @Column(name="TYPE")
    private String type;

    @ApiModelProperty(value = "机构编码")
    @Column(name="ORG_CODE")
    private String orgCode;

    @ApiModelProperty(value = "工号")
    @Column(name="JOB_NUMBER")
    private String jobNumber;

}