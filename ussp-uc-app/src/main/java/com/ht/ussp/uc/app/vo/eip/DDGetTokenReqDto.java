package com.ht.ussp.uc.app.vo.eip;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取钉钉token
 * @author:喻尊龙
 * @date: 2018/4/18
 */
@Data
public class DDGetTokenReqDto implements Serializable {
    private static final long serialVersionUID = -3392739896853139850L;

    private String corpid;

    private String corpsecret;
}
