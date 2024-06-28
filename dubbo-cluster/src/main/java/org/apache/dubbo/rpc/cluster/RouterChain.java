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
package org.apache.dubbo.rpc.cluster;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.dubbo.rpc.cluster.Constants.ROUTER_KEY;

/**
 * Router chain
 */
public class RouterChain<T> {

    // full list of addresses from registry, classified by method name.
    // xjh-在RegistryDirectory刷新refreshInvoker时会将最新的invoker设置到这里
    private List<Invoker<T>> invokers = Collections.emptyList();

    // containing all routers, reconstruct every time 'route://' urls change.
    // xjh-当前 RouterChain 中真正要使用的 Router 集合，其中不仅包括了builtinRouters 集合中全部的 Router 对象，还包括通过 addRouters() 方法添加的 Router 对象
    private volatile List<Router> routers = Collections.emptyList();

    // Fixed router instances: ConfigConditionRouter, TagRouter, e.g., the rule for each instance may change but the
    // instance will never delete or recreate.
    // xjh-固定的routers实例，当前 RouterChain 激活的内置 Router 集合，永远不会被删除或者重新创建
    private List<Router> builtinRouters = Collections.emptyList();

    public static <T> RouterChain<T> buildChain(URL url) {
        // xjh-静态方法创建RouteChain
        return new RouterChain<>(url);
    }

    private RouterChain(URL url) {
        List<RouterFactory> extensionFactories = ExtensionLoader.getExtensionLoader(RouterFactory.class)
                .getActivateExtension(url, ROUTER_KEY);

        // xjh-根据所有激活的RouterFactory生成相应的Router
        List<Router> routers = extensionFactories.stream()
                .map(factory -> factory.getRouter(url))
                .collect(Collectors.toList());

        // xjh-初始化builtinRouters字段以及routers字段
        initWithRouters(routers);
    }

    /**
     * the resident routers must being initialized before address notification.
     * FIXME: this method should not be public
     */
    public void initWithRouters(List<Router> builtinRouters) {
        // xjh-将初始加载的routers赋值给这两个字段并排序
        this.builtinRouters = builtinRouters;
        this.routers = new ArrayList<>(builtinRouters);
        this.sort();
    }

    /**
     * If we use route:// protocol in version before 2.7.0, each URL will generate a Router instance, so we should
     * keep the routers up to date, that is, each time router URLs changes, we should update the routers list, only
     * keep the builtinRouters which are available all the time and the latest notified routers which are generated
     * from URLs.
     *
     * xjh-添加新的routers，新的route协议的url将会创建新的router实例。
     *
     * @param routers routers from 'router://' rules in 2.6.x or before.
     */
    public void addRouters(List<Router> routers) {
        List<Router> newRouters = new ArrayList<>();
        // xjh-将第一次加载的内置routers与新来的routers放入到routers中并排序
        newRouters.addAll(builtinRouters);
        newRouters.addAll(routers);
        CollectionUtils.sort(newRouters);
        this.routers = newRouters;
    }

    private void sort() {
        Collections.sort(routers);
    }

    /**
     *
     * @param url
     * @param invocation
     * @return
     */
    public List<Invoker<T>> route(URL url, Invocation invocation) {
        List<Invoker<T>> finalInvokers = invokers;
        for (Router router : routers) {
            // xjh-遍历所有routers的route方法
            finalInvokers = router.route(finalInvokers, url, invocation);
        }
        return finalInvokers;
    }

    /**
     * Notify router chain of the initial addresses from registry at the first time.
     * Notify whenever addresses in registry change.
     */
    public void setInvokers(List<Invoker<T>> invokers) {
        this.invokers = (invokers == null ? Collections.emptyList() : invokers);
        // xjh-调用每个router的notify方法
        routers.forEach(router -> router.notify(this.invokers));
    }
}
