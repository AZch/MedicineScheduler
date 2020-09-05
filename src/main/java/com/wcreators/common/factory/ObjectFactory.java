package com.wcreators.common.factory;

import com.wcreators.common.ApplicationContext;
import com.wcreators.common.annotations.PostConstruct;
import com.wcreators.common.configurators.object.ObjectConfigurator;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ObjectFactory {
    private final ApplicationContext context;
    private final List<ObjectConfigurator> configurators = new ArrayList<>();

    @SneakyThrows
    public ObjectFactory(ApplicationContext context) {
        this.context = context;
        for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)) {
            configurators.add(aClass.getDeclaredConstructor().newInstance());
        }
    }

    public <T> T createObject(Class<T> implClass) {
        T t = create(implClass);

        configure(t);

        invokeInit(implClass, t);

        return t;
    }

    @SneakyThrows
    private <T> void invokeInit(Class<T> implClass, T t) {
        for (Method method : implClass.getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(t);
            }
        }
    }

    private <T> void configure(T t) {
        configurators.forEach(objectConfigurator -> objectConfigurator.configure(t, context));
    }

    @SneakyThrows
    private <T> T create(Class<T> implClass) {
        return implClass.getDeclaredConstructor().newInstance();
    }
}
