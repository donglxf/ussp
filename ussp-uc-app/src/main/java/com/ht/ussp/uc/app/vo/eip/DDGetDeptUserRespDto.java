package com.ht.ussp.uc.app.vo.eip;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 获取钉钉部门人员
 * @author:喻尊龙
 * @date: 2018/4/18
 */
@Data
public class DDGetDeptUserRespDto implements Serializable {
    private static final long serialVersionUID = -9172345561032206880L;

    List<Userlist> userlist;
}
@Data
class Userlist implements Serializable{

    private static final long serialVersionUID = -7029823235015910980L;
    private String unionid;
    private String openId;
    private String remark;
    private String userid;
    private boolean isBoss;
    private String tel;
    private String department;
    private String workPlace;
    private String email;
    private String order;
    private String dingId;
    private boolean isLeader;
    private String mobile;
    private boolean active;
    private boolean isAdmin;
    private String avatar;
    private boolean isHide;
    private String jobnumber;
    private String name;
    private String position;
}
