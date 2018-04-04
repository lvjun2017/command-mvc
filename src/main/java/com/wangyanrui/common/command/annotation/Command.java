package com.wangyanrui.common.command.annotation;

import java.lang.annotation.*;

/**
 * You Command Component and method must be annotation
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Command {

    /**
     * The value may indicate a suggestion for a logical component name,
     *
     * @return the suggested component name, if any (or empty String otherwise)
     */
    String value() default "";
}
