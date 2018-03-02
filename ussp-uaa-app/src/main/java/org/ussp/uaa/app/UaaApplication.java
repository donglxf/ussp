package org.ussp.uaa.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @ClassName: UaaApplication
 * @Description: 用户鉴权中心
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月2日 下午2:21:35
 */

@SpringBootApplication
public class UaaApplication 
{
    public static void main( String[] args )
    {
        SpringApplication.run(UaaApplication.class, args);
    }
}
