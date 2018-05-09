package com.ht.ussp.config;


import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.resolver.DefaultPropertyResolver;

/**
 * 
 * @ClassName: JasyptEncryptionConfiguration
 * @Description: 加密配置--配置文件属性
 * @author wim qiuwenwu@hongte.info
 * @date 2018年5月9日 下午1:12:43
 */
@Configuration
public class JasyptEncryptionConfiguration {
		
		@Bean("lazyJasyptStringEncryptor")
	    public StringEncryptor stringEncryptor(@Value("${jasypt.encryptor.password}") String password) {
	        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
	        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
	        config.setPassword(password);
	        config.setAlgorithm("PBEWithMD5AndDES");
	        config.setKeyObtentionIterations("1000");
	        config.setPoolSize("1");
	        config.setProviderName("SunJCE");
	        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
	        config.setStringOutputType("base64");
	        encryptor.setConfig(config);
	        return encryptor;
	    }
	    
		@Bean(name="encryptablePropertyResolver")
	    public EncryptablePropertyResolver encryptablePropertyResolver( StringEncryptor stringEncryptor) {
	        return new JasyptEncryptablePropertyResolver(stringEncryptor);
	    }
	    
	    private class JasyptEncryptablePropertyResolver extends DefaultPropertyResolver implements EncryptablePropertyResolver {

	        private final StringEncryptor encryptor;

	        public JasyptEncryptablePropertyResolver(StringEncryptor encryptor) {
	            super(encryptor);

	            this.encryptor = encryptor;
	        }

	        @Override
	        public String resolvePropertyValue(String value) {
	            return super.resolvePropertyValue(value);
	        }
	    }

}
