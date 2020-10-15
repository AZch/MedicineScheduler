package com.wcreators.common.factory;

import com.wcreators.common.ApplicationContext;
import com.wcreators.common.annotations.PostInit;
import com.wcreators.common.annotations.PostConstruct;
import com.wcreators.common.configurators.object.ObjectConfigurator;
import com.wcreators.common.configurators.proxy.ProxyConfigurator;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ObjectFactory {
    private final ApplicationContext context;
    private final List<ObjectConfigurator> configurators = new ArrayList<>();
    private final List<ProxyConfigurator> proxyConfigurators = new ArrayList<>();

    @SneakyThrows
    public ObjectFactory(ApplicationContext context) {
        this.context = context;
        for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)) {
            if (!aClass.isAnnotationPresent(PostInit.class)) {
                configurators.add(aClass.getDeclaredConstructor().newInstance());
            }
        }
        for (Class<? extends ProxyConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ProxyConfigurator.class)) {
            if (!aClass.isAnnotationPresent(PostInit.class)) {
                ProxyConfigurator proxyConfigurator = aClass.getDeclaredConstructor().newInstance();
                configure(proxyConfigurator);
                proxyConfigurators.add(proxyConfigurator);
            }
        }
    }

    @SneakyThrows
    public void postInit() {
        for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)) {
            if (aClass.isAnnotationPresent(PostInit.class)) {
                configurators.add(aClass.getDeclaredConstructor(ApplicationContext.class).newInstance(context));
            }
        }
        for (Class<? extends ProxyConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ProxyConfigurator.class)) {
            if (aClass.isAnnotationPresent(PostInit.class)) {
                ProxyConfigurator proxyConfigurator = aClass.getDeclaredConstructor(ApplicationContext.class).newInstance(context);
                configure(proxyConfigurator);
                proxyConfigurators.add(proxyConfigurator);
            }
        }
    }

    public <T> T createObject(Class<T> implClass) {
        T t = create(implClass);

        configure(t);

        invokeInit(implClass, t);

        t = wrapWithProxy(implClass, t);

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

    @SuppressWarnings("unchecked")
    private <T> T wrapWithProxy(Class<T> implClass, T t) {
        for (ProxyConfigurator proxyConfigurator : proxyConfigurators) {
            t = (T) proxyConfigurator.replaceWithProxy(t, implClass);
        }
        return t;
    }

    @SneakyThrows
    private <T> T create(Class<T> implClass) {
        return implClass.getDeclaredConstructor().newInstance();
    }
}
