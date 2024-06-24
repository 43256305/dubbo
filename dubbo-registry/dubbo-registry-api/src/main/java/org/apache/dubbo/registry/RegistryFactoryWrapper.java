/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.registry;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

import java.util.Collections;

//xjh-为RegistryFactory的Wrapper类，用于在真正的RegistryFactory::getRegistry操作之前提供一些逻辑。
// 如这里，他在getRegistry获取Registry时将真正的Registry封装在了ListenerRegistryWrapper中并返回
// ListenerRegistryWrapper在Registry的方法周围加上了一些逻辑，他会在真正的Registry的register()、subscribe()等等调用之后将这些事件通知到RegistryServiceListener监听器
public class RegistryFactoryWrapper implements RegistryFactory {
    private RegistryFactory registryFactory;

    public RegistryFactoryWrapper(RegistryFactory registryFactory) {
        this.registryFactory = registryFactory;
    }

    @Override
    public Registry getRegistry(URL url) {
        // xjh-将真正的Registry封装在了ListenerRegistryWrapper中并返回
        return new ListenerRegistryWrapper(registryFactory.getRegistry(url),
                // xjh-通过SPI机制获取当前url激活的RegistryServiceListener列表
                Collections.unmodifiableList(ExtensionLoader.getExtensionLoader(RegistryServiceListener.class)
                        .getActivateExtension(url, "registry.listeners")));
    }
}
