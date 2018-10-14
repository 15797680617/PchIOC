package com.ch.annotation;

import java.lang.annotation.*;

/**
 * @Auther: pch
 * @Date: 2018/10/14 14:30
 * @Description:
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
}
