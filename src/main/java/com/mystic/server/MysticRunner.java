package com.mystic.server;

import lombok.Data;

import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Data
public class MysticRunner {
	ApplicationContext context;
	String endpoint;
	String folder;
	
	public void init() {
		Assert.assertNotNull("[Endpoint] Cannot Be Null", endpoint);
		System.setProperty("mystic.endpoint", endpoint);
		if(folder != null) {
			System.setProperty("mystic.data", folder);
		}
		context = new ClassPathXmlApplicationContext("spring/ApplicationContext.xml");
		context.getBean("mysticServer");
	}
}
