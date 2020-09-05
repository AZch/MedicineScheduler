package com.wcreators.common.configurators.object;

import com.wcreators.common.ApplicationContext;

public interface ObjectConfigurator {
    void configure(Object t, ApplicationContext context);
}
