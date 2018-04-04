package com.wangyanrui.common.command.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationUtil {

    /**
     * check one class or method has special annotation label
     *
     * @param element
     * @param annotationClass
     * @return
     */
    public static boolean hasAnnotation(AnnotatedElement element,
                                        Class<? extends Annotation> annotationClass) {

        return element.isAnnotationPresent(annotationClass);
    }

}
