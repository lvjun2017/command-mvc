package com.wangyanrui.common.command;

import java.lang.annotation.*;

/**
 * You Command Component must be annotation
 * You Command Component's Method cloud be annotation
 * (We will add an attribute to custom action key in the future)
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {
}
