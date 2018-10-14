package com.ch.demo;

import com.ch.annotation.Component;

/**
 * @Auther: pch
 * @Date: 2018/10/14 15:14
 * @Description:
 */
@Component
public class Person {
	private String name;
	private String sex;
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
