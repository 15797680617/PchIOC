package com.ch.annotation;

import java.lang.annotation.*;

/**
 * @Auther: pch
 * @Date: 2018/10/14 14:28
 * @Description:
 */
// 实例化类对象
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

}
