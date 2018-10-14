package demo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.ch.annotation.Autowired;
import com.ch.annotation.Component;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * @Auther: pch
 * @Date: 2018/10/14 14:01
 * @Description:
 */
public class DemoTest {

	private List<Class> classes = new LinkedList<>();
	private Map<String, Object> componentMap = new HashMap();

	// 使用hutool 解析xml
	@Test

	public void fun1() {
		// 读取当前项目resources目录下的 spring-config.xml文件
		InputStream inputStream = this.getClass().getResourceAsStream("/spring-config.xml");
		Document document = XmlUtil.readXML(inputStream);
		Element rootElement = XmlUtil.getRootElement(document);
		Element element = XmlUtil.getElement(rootElement, "context:component-scan");
		Attr node = element.getAttributeNode("base-package");
		String baseScan = node.getValue();
		System.out.println(baseScan);
		// 扫描包
		doScan(baseScan);
		// 将加了注解的类 进行实例化 放到map中
		instantiation();
		autoFields();
		System.out.println(1);
	}

	// 将加了注解的类 进行实例化 放到map中
	private void instantiation(){
		Object instance;
		for (Class aClass : classes) {
			// 判断是否有注解
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
			componentMap.put(aClass.getName(),instance);
		}
	}

	// 剖析类的成员对象 自动注入
	private void autoFields(){
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

	// 扫描包
	private void doScan(String packageName){
		packageName = packageName.replaceAll("\\.", "/");
		URL url = this.getClass().getResource("/" + packageName);
		String urlFile = url.getFile();
		File file = new File(urlFile);
		File[] files = file.listFiles();
		for (File childrenFile : files) {
			if (childrenFile.isDirectory()) {
				doScan(packageName + "/" + childrenFile.getName());
			}else{
				// 得到类的全限定名
				String className = (packageName.replaceAll("/", ".")) + "." + (childrenFile.getName().replace(".class", ""));
				System.out.println(className);
				try {
					classes.add(Class.forName(className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
