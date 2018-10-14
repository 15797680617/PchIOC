package com.ch.servlet;

import com.ch.demo.DemoTest;
import com.ch.utils.Util;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @Auther: pch
 * @Date: 2018/10/14 13:55
 * @Description:
 */
public class MyServlet extends HttpServlet {


	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init(ServletConfig config) {
		// 初始化 IOC容器
		Util.init();
		// 接下来 我们不直接new对象 试试从容器中拿
		DemoTest demoTest = (DemoTest) Util.componentMap.get(DemoTest.class.getName());
		System.out.println(demoTest);
		//输出 com.ch.demo.DemoTest@17736fd8

		// 看看DemoTest的Person属性 是否也注入成功
		System.out.println(demoTest.getPerson());
		//输出 com.ch.demo.Person@27edd20d
	}

}
