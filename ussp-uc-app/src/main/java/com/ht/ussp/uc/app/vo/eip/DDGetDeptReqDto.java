package com.ht.ussp.uc.app.vo.eip;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取钉钉部门
 * @author:喻尊龙
 * @date: 2018/4/18
 */
@Data
public class DDGetDeptReqDto implements Serializable {
    private static final long serialVersionUID = 6216464288926873845L;

    private String token;

    private String authDept;
}
