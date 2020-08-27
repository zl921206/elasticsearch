package com.kamluen.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@EnableAutoConfiguration(exclude = {FreeMarkerAutoConfiguration.class, DataSourceAutoConfiguration.class})
@SpringBootApplication
@ComponentScan(basePackages = {"com.kamluen.elasticsearch","com.kamluen.odps.service"})
@ImportResource(locations= {"classpath:applicationContext-redis.xml"})
public class ElasticsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticsearchApplication.class, args);
	}

//	@Bean
//	public EncryptBeanPostProcessor encryptBeanPostProcessor(){
//		EncryptBeanPostProcessor processor = new EncryptBeanPostProcessor();
//		return processor;
//	}
}
