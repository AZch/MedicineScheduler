package com.wcreators.common.configurators.proxy;

public interface ProxyConfigurator {
    Object replaceWithProxy(Object t, Class<?> implClass);
}
