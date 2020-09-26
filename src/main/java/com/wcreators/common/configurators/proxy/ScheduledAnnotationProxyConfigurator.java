package com.wcreators.common.configurators.proxy;

import com.wcreators.common.annotations.EnableScheduling;
import com.wcreators.common.annotations.InjectProperty;
import com.wcreators.common.annotations.Scheduled;
import com.wcreators.common.annotations.Singleton;
import lombok.SneakyThrows;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledAnnotationProxyConfigurator implements ProxyConfigurator {
    @InjectProperty("SCHEDULE_INTERVAL")
    private Integer interval;

    private final Map<Method, ScheduledExecutorService> executors = new ConcurrentHashMap<>();

    @Override
    public Object replaceWithProxy(Object t, Class<?> implClass) {
        if (implClass.isAnnotationPresent(EnableScheduling.class) && implClass.isAnnotationPresent(Singleton.class)) {

            if (implClass.getInterfaces().length == 0) {
                return Enhancer.create(implClass, (InvocationHandler) (o, method, args) -> getInvocationHandler(method, args, t));
            }

            return Proxy.newProxyInstance(implClass.getClassLoader(), implClass.getInterfaces(), (proxy, method, args) -> getInvocationHandler(method, args, t));

        }

        return t;
    }

    @SneakyThrows
    private Object getInvocationHandler(Method method, Object[] args, Object t) {
        Method classMethod = t.getClass().getMethod(method.getName());
        boolean isMethodHaveScheduledAnnotation = classMethod.isAnnotationPresent(Scheduled.class);
        if (isMethodHaveScheduledAnnotation) {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

            Runnable task = () -> {
                try {
                    method.invoke(t, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.err.println("task interrupted");
                }
            };

            if (executors.containsKey(classMethod)) {
                System.out.println("already have");
                return null;
            }

            executor.scheduleWithFixedDelay(task, 0, interval, TimeUnit.SECONDS);
            executors.put(classMethod, executor);
            return null;
        }
        return method.invoke(t, args);
    }
}
