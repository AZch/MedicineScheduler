package com.wcreators.common.configurators.object;

import com.wcreators.common.ApplicationContext;
import com.wcreators.common.annotations.InjectByType;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class InjectByTypeAnnotationObjectConfigurator implements ObjectConfigurator {
    @Override
    @SneakyThrows
    public void configure(Object t, ApplicationContext context) {
     for (Field field : t.getClass().getDeclaredFields()) {
         InjectByType annotation = field.getAnnotation(InjectByType.class);
         if (annotation != null) {
             field.setAccessible(true);
             Class<?> objectType = annotation.value() == Object.class ? field.getType() : annotation.value();
             Object object = context.getObject(objectType);
             field.set(t, object);
         }
     }
    }
}
