package com.ht.ussp.uc.app.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String resCode;

    private String resNameCn;

    private int sequence;

    private String resType;

    private String resParent;

    private String resContent;

    private String fontIcon;

    private String status;
}
