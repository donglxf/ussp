package com.ht.ussp.uc.app.vo.eip;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取钉钉部门人员
 * @author:喻尊龙
 * @date: 2018/4/18
 */
@Data
public class DDGetDeptUserReqDto implements Serializable {
    private static final long serialVersionUID = -6095439322143040231L;

    private String token;

    private String departmentId;
}
