package com.ht.ussp.uc.app.vo.eip;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取钉钉授权部门
 * @author:喻尊龙
 * @date: 2018/4/18
 */
@Data
public class DDGetAuthDeptRespDto implements Serializable {
    private static final long serialVersionUID = -9214302664281730363L;

    private String authedDept;

}
