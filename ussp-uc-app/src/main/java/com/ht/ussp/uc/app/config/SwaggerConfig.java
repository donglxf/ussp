package com.ht.ussp.uc.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
* @ClassName: SwaggerConfig
* @Description: 用于展示resful接口
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午5:53:49
 */
@Configuration  
@EnableSwagger2  
public class SwaggerConfig {@Bean
    public Docket createRestApi() {
    return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.ht.ussp.uc.app"))
            .paths(PathSelectors.any())
            .build();
}

private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
            .title("统一用户中心")
            .contact("qiuwenwu@hongte.info")
            .version("1.0")
            .build();
}

}
