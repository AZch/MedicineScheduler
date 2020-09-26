package com.wcreators.common.configurators.object;

import com.wcreators.common.ApplicationContext;
import com.wcreators.common.annotations.InjectProperty;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class InjectPropertyAnnotationObjectConfigurator implements ObjectConfigurator {
    private Map<String, String> propertiesMap;

    @SneakyThrows
    public InjectPropertyAnnotationObjectConfigurator() {
        String path = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("application.properties")).getPath();
        Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
        propertiesMap = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0].trim(), arr -> arr[1].trim()));
    }

    @SneakyThrows
    @Override
    public void configure(Object t, ApplicationContext context) {
        Class<?> implClass = t.getClass();
        for (Field field : implClass.getDeclaredFields()) {
            InjectProperty annotation = field.getAnnotation(InjectProperty.class);
            if (annotation != null) {
                String value = annotation.value().isEmpty() ? propertiesMap.get(field.getName()) : propertiesMap.get(annotation.value());
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                if (fieldType == Integer.class) {
                    field.set(t, Integer.parseInt(value));
                } else if (fieldType == Boolean.class) {
                    field.set(t, Boolean.valueOf(value));
                } else {
                    field.set(t, value);
                }
            }
        }
    }
}
