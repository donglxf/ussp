package com.ht.ussp.uc.app.vo.eip;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 获取钉钉部门
 * @author:喻尊龙
 * @date: 2018/4/18
 */
@Data
public class DDGetDeptRespDto implements Serializable {
    private static final long serialVersionUID = -7889529754327793890L;

    List<Department> department;
}
@Data
class Department implements Serializable{

    private static final long serialVersionUID = -8910154111441066446L;

    private boolean createDeptGroup;
    private String name;
    private String id;
    private boolean autoAddUser;
    private String parentid;
}
