package com.ht.ussp.uc.app.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.context.annotation.Configuration;

/**
 * 
* @ClassName: MySQLUpperCaseStrategy
* @Description: 将表名转换成大写
* @author wim qiuwenwu@hongte.info
* @date 2018年1月5日 下午8:23:26
 */
@Configuration
public class MySQLUpperCaseStrategy extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = 7571583531433930023L;
	@SuppressWarnings("static-access")
	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        String tableName = name.getText().toUpperCase();
         
        return name.toIdentifier(tableName);
    }
}
