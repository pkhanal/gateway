/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.gateway.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.annotation.Resource;
import org.kaazing.gateway.service.ServiceContext;
import static java.util.Collections.synchronizedSet;
import static java.util.Collections.unmodifiableList;
import static java.util.ServiceLoader.load;

public final class GatewayObserver implements GatewayObserverApi {
    private final List<GatewayObserverFactorySpi> gatewayListenerSpi;

    private GatewayObserver(Set<GatewayObserverFactorySpi> gatewayListenerSpis) {
        List<GatewayObserverFactorySpi> list = new ArrayList<>(gatewayListenerSpis);
        this.gatewayListenerSpi = unmodifiableList(list);
    }

    public static GatewayObserver newInstance(Map<String, Object> injectables) {
        return newInstance(load(GatewayObserverFactorySpi.class), injectables);
    }

    public static GatewayObserver newInstance(ClassLoader loader, Map<String, Object> injectables) {
        return newInstance(load(GatewayObserverFactorySpi.class, loader), injectables);
    }

    private static GatewayObserver newInstance(ServiceLoader<GatewayObserverFactorySpi> gatewayListenerSpis,
                                               Map<String, Object> injectables) {
        Set<GatewayObserverFactorySpi> gatewayListenerSpiList = synchronizedSet(new HashSet<GatewayObserverFactorySpi>());
        for (GatewayObserverFactorySpi gatewayListenerSpi : gatewayListenerSpis) {
            gatewayListenerSpiList.add(gatewayListenerSpi);
        }

        for (GatewayObserverFactorySpi gatewayListenerSpi : gatewayListenerSpis) {
            injectResources(gatewayListenerSpi, injectables);
        }
        return new GatewayObserver(gatewayListenerSpiList);
    }

    private static void injectResources(Object target, Map<String, Object> values) {
        if (target == null) {
            return;
        }

        Class<?> clazz = target.getClass();

        for (Method method : clazz.getMethods()) {
            Resource resource = method.getAnnotation(Resource.class);
            if (resource != null) {
                String name = resource.name();
                Object val = values.get(name);
                try {
                    method.invoke(target, val);
                } catch (Exception e) {
                    // TODO ?
                }
            }
        }
    }

    @Override
    public void initingService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.initingService(serviceContext);
        }
    }

    @Override
    public void initedService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.initedService(serviceContext);
        }
    }

    @Override
    public void startingService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.startingService(serviceContext);
        }
    }

    @Override
    public void startedService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.startedService(serviceContext);
        }
    }

    @Override
    public void stopingService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.stopingService(serviceContext);
        }
    }

    @Override
    public void stoppedService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.stoppedService(serviceContext);
        }
    }

    @Override
    public void quiesceingService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.quiesceingService(serviceContext);
        }
    }

    @Override
    public void quiescedService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.quiescedService(serviceContext);
        }
    }

    @Override
    public void destroyingService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.destroyingService(serviceContext);
        }
    }

    @Override
    public void destroyedService(ServiceContext serviceContext) {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.destroyedService(serviceContext);
        }
    }

    @Override
    public void startingGateway() {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.startingGateway();
        }
    }

    @Override
    public void stoppedGateway() {
        for (GatewayObserverApi gatewayListener : gatewayListenerSpi) {
            gatewayListener.stoppedGateway();
        }
    }
}
