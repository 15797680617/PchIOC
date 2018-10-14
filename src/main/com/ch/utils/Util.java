package com.ch.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.ch.annotation.Autowired;
import com.ch.annotation.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: pch
 * @Date: 2018/10/14 14:25
 * @Description:
 */
public class Util {

	public static List<Class> classes = new LinkedList<>();
	public static Map<String, Object> componentMap = new HashMap();

	// 扫描包下的类 初始化IOC容器
	public static void init() {
		// 读取当前项目resources目录下的 spring-config.xml文件
		InputStream inputStream = Util.class.getResourceAsStream("/spring-config.xml");
		if (inputStream == null) {
			return;
		}
		Document document = XmlUtil.readXML(inputStream);
		if (document == null) {
			return;
		}
		Element rootElement = XmlUtil.getRootElement(document);
		if (rootElement == null) {
			return;
		}
		Element element = XmlUtil.getElement(rootElement, "context:component-scan");
		if (element == null) {
			return;
		}
		Attr node = element.getAttributeNode("base-package");
		if (node == null) {
			return;
		}
		String baseScan = node.getValue();
		if (StrUtil.isEmpty(baseScan)) {
			return;
		}
		// 扫描包下所有的类
		doScan(baseScan);
		// 实例化类
		instantiation();
		// 剖析类的成员对象 自动注入
		autoFields();
	}

	// 剖析类的成员对象 自动注入
	private static void autoFields(){
		// 暴力反射 获取私有属性
		Field[] fields;
		for (Map.Entry<String, Object> entry : componentMap.entrySet()) {
			fields = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				// 判断属性是否有Autowired注解
				System.out.println(field.isAnnotationPresent(Autowired.class));
				if (!field.isAnnotationPresent(Autowired.class)){
					continue;
				}
				// 判断 要注入的对象 IOC容器中是否存在
				if (!componentMap.containsKey(field.getType().getName())) {
					continue;
				}
				// 注入对象
				try {
					Object o = entry.getValue();
					Object pp = componentMap.get(field.getType().getName());
					// 允许访问私有属性
					field.setAccessible(true);
					field.set(entry.getValue(),componentMap.get(field.getType().getName()));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 将加了注解的类 进行实例化 放到map中
	private static void instantiation(){
		Object instance;
		Field[] fields;
		for (Class aClass : classes) {
			// 判断是否有Component注解
			if (!aClass.isAnnotationPresent(Component.class)) {
				continue;
			}
			instance = null;
			try {
				instance = aClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (instance == null) {
				continue;
			}
			// 将类名作为key 实例化对象为value
			componentMap.put(aClass.getName(),instance);
		}
	}

	// 扫描包
	private static void doScan(String packageName){
		packageName = packageName.replaceAll("\\.", "/");
		URL url = Util.class.getResource("/" + packageName);
		String urlFile = url.getFile();
		File file = new File(urlFile);
		File[] files = file.listFiles();
		for (File childrenFile : files) {
			if (childrenFile.isDirectory()) {
				doScan(packageName + "/" + childrenFile.getName());
			}else{
				// 得到类的全限定名
				String className = (packageName.replaceAll("/", ".")) + "." + (childrenFile.getName().replace(".class", ""));
				try {
					classes.add(Class.forName(className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
