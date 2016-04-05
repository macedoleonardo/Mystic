package com.mystic.test;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MysticServerTest {

	@Ignore
	@Test
	public void test() {
		new ClassPathXmlApplicationContext("spring/Application-Context.xml");
	}

}
