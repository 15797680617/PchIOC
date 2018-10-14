package com.ch.demo;

import com.ch.annotation.Autowired;
import com.ch.annotation.Component;

/**
 * @Auther: pch
 * @Date: 2018/10/14 15:13
 * @Description:
 */
@Component
public class DemoTest {

	@Autowired
	private Person person;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
