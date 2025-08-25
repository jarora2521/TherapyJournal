package com.therapy.nest.shared.utils;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class DemoHelper {
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(source.getClass()))
                .map(PropertyDescriptor::getName)
                .filter(name -> {
                    try {
                        Method getter = source.getClass().getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
                        return getter.invoke(source) == null;
                    } catch (Exception e) {
                        return true;
                    }
                })
                .toArray(String[]::new);
    }
}
