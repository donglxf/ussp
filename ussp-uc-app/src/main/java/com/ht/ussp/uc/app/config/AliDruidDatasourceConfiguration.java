package com.ht.ussp.uc.app.config;


import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 
 * @ClassName: AliDruidDatasourceConfiguration
 * @Description: druid配置
 * @author wim qiuwenwu@hongte.info
 * @date 2018年3月7日 上午11:01:39
 */

@Configuration
public class AliDruidDatasourceConfiguration {
	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;
	
	@Value("${spring.datasource.initialSize}")
	private int initialSize;
	
	@Value("${spring.datasource.maxActive}")
	private int maxActive;
	
	@Value("${spring.datasource.minIdle}")
	private int minIdle;
	
	@Value("${spring.datasource.validation-query}")
	private String validation_query;
	
	@Value("${spring.datasource.filters}")
	private String filters;
		
	@Value("${spring.datasource.testOnBorrow}")
	private boolean testOnBorrow;
	
	@Value("${spring.datasource.connection-properties}")
	private String connection_properties;
    
    @Bean
    @Primary
    public DataSource dataSource(){
        DruidDataSource datasource = new DruidDataSource();
        
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        
        //configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setValidationQuery(validation_query);
        datasource.setTestOnBorrow(testOnBorrow);
        try {
            datasource.setFilters(filters);
        } catch (SQLException ex) {
//            LOG.error("Exception occurs while settig Druid filters", ex);
        }
        
        return datasource;
    }
}
