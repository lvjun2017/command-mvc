package com.wangyanrui.common.command.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.Map;

public class AnnotationUtil {

    /**
     * check one class has special annotation label
     *
     * @param element         Class
     * @param annotationClass Annotation's Class
     * @return
     */
    public static boolean hasAnnotation(AnnotatedElement element,
                                        Class<? extends Annotation> annotationClass) {

        return element.isAnnotationPresent(annotationClass);
    }

    /**
     * edit one annotation field's value
     *
     * @param handler
     * @param targetField
     * @param updateValue
     */
    public static void editAnnotationField(InvocationHandler handler, String targetField, Object updateValue) throws NoSuchFieldException, IllegalAccessException {
        Field field = handler.getClass().getDeclaredField("memberValues");
        field.setAccessible(true);
        Map<String, Object> memberValues = (Map) field.get(handler);
        memberValues.put(targetField, updateValue);
    }

}
