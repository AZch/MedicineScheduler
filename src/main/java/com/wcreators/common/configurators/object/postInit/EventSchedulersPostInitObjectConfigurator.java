package com.wcreators.common.configurators.object.postInit;

import com.wcreators.common.ApplicationContext;
import com.wcreators.common.annotations.PostInit;
import com.wcreators.common.annotations.eventSchedulers.EventSchedulers;
import com.wcreators.common.annotations.eventSchedulers.UsingEventSheduler;
import com.wcreators.common.configurators.object.ObjectConfigurator;
import com.wcreators.domain.scheduler.Scheduler;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@PostInit
public class EventSchedulersPostInitObjectConfigurator implements ObjectConfigurator {
    private final List<Scheduler> eventSchedulers = new ArrayList<>();

    public EventSchedulersPostInitObjectConfigurator(ApplicationContext context) {
        for (Class<? extends Scheduler> sClass : context.getConfig().getScanner().getSubTypesOf(Scheduler.class)) {
            if (sClass.isAnnotationPresent(UsingEventSheduler.class)) {
                Scheduler scheduler = context.getObject(sClass);
                eventSchedulers.add(scheduler);
            }
        }
    }

    @SneakyThrows
    @Override
    public void configure(Object t, ApplicationContext context) {
        for (Field field : t.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(EventSchedulers.class)) {
                field.setAccessible(true);
                field.set(t, eventSchedulers);
            }
        }
    }
}
