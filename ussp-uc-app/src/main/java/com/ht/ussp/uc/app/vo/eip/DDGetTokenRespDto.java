package com.ht.ussp.uc.app.vo.eip;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取钉钉token
 * @author:喻尊龙
 * @date: 2018/4/18
 */
@Data
public class DDGetTokenRespDto implements Serializable {
    private static final long serialVersionUID = 1318245243621719958L;

    @JSONField(name = "access_token")
    private String accessToken;
}
