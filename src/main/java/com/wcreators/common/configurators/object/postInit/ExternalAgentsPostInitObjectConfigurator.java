package com.wcreators.common.configurators.object.postInit;

import com.wcreators.common.ApplicationContext;
import com.wcreators.common.annotations.PostInit;
import com.wcreators.common.annotations.externalAgents.ExternalAgnts;
import com.wcreators.common.annotations.externalAgents.UsingExternalAgent;
import com.wcreators.common.configurators.object.ObjectConfigurator;
import com.wcreators.externalAgent.ExternalAgent;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@PostInit
public class ExternalAgentsPostInitObjectConfigurator implements ObjectConfigurator {
    private final List<ExternalAgent> externalAgents = new ArrayList<>();

    public ExternalAgentsPostInitObjectConfigurator(ApplicationContext context) {
        for (Class<? extends ExternalAgent> eaClass : context.getConfig().getScanner().getSubTypesOf(ExternalAgent.class)) {
            if (eaClass.isAnnotationPresent(UsingExternalAgent.class)) {
                ExternalAgent externalAgent = context.getObject(eaClass);
                externalAgents.add(externalAgent);
            }
        }
    }

    @SneakyThrows
    @Override
    public void configure(Object t, ApplicationContext context) {
        for (Field field : t.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ExternalAgnts.class)) {
                field.setAccessible(true);
                field.set(t, externalAgents);
            }
        }
    }
}
