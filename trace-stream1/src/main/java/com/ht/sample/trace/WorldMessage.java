package com.ht.sample.trace;

/**
 * 摘要:
 *
 * @author xyt
 * @create 2017-12-20 下午9:42
 **/

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author billjiang 475572229@qq.com
 * @create 17-8-22
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorldMessage {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

}