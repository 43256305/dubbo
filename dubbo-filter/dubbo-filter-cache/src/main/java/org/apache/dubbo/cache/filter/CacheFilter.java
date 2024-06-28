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
package org.apache.dubbo.cache.filter;

import org.apache.dubbo.cache.Cache;
import org.apache.dubbo.cache.CacheFactory;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import java.io.Serializable;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;
import static org.apache.dubbo.common.constants.FilterConstants.CACHE_KEY;

/**
 * CacheFilter is a core component of dubbo.Enabling <b>cache</b> key of service,method,consumer or provider dubbo will cache method return value.
 * Along with cache key we need to configure cache type. Dubbo default implemented cache types are
 * <li>lru</li>
 * <li>threadlocal</li>
 * <li>jcache</li>
 * <li>expiring</li>
 *
 * xjh-dubbo提供的缓存拦截器。它的主要作用是通过在消费者（Consumer）端对服务调用结果进行缓存，减少对服务提供者（Provider）的频繁调用，从而提升系统性能，减轻后端负载。
 * 实现有：lru/threadlocal/jcache/expiring，默认为lru
 *
 * <pre>
 *   e.g. 1)&lt;dubbo:service cache="lru" /&gt;
 *        2)&lt;dubbo:service /&gt; &lt;dubbo:method name="method2" cache="threadlocal" /&gt; &lt;dubbo:service/&gt;
 *        3)&lt;dubbo:provider cache="expiring" /&gt;
 *        4)&lt;dubbo:consumer cache="jcache" /&gt;
 *
 *If cache type is defined in method level then method level type will get precedence. According to above provided
 *example, if service has two method, method1 and method2, method2 will have cache type as <b>threadlocal</b> where others will
 *be backed by <b>lru</b>
 *</pre>
 *
 * @see org.apache.dubbo.rpc.Filter
 * @see org.apache.dubbo.cache.support.lru.LruCacheFactory
 * @see org.apache.dubbo.cache.support.lru.LruCache
 * @see org.apache.dubbo.cache.support.jcache.JCacheFactory
 * @see org.apache.dubbo.cache.support.jcache.JCache
 * @see org.apache.dubbo.cache.support.threadlocal.ThreadLocalCacheFactory
 * @see org.apache.dubbo.cache.support.threadlocal.ThreadLocalCache
 * @see org.apache.dubbo.cache.support.expiring.ExpiringCacheFactory
 * @see org.apache.dubbo.cache.support.expiring.ExpiringCache
 *
 */
@Activate(group = {CONSUMER, PROVIDER}, value = CACHE_KEY)
public class CacheFilter implements Filter {

    private CacheFactory cacheFactory;

    /**
     * Dubbo will populate and set the cache factory instance based on service/method/consumer/provider configured
     * cache attribute value. Dubbo will search for the class name implementing configured <b>cache</b> in file org.apache.dubbo.cache.CacheFactory
     * under META-INF sub folders.
     *
     * @param cacheFactory instance of CacheFactory based on <b>cache</b> type
     */
    public void setCacheFactory(CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    /**
     * If cache is configured, dubbo will invoke method on each method call. If cache value is returned by cache store
     * then it will return otherwise call the remote method and return value. If remote method's return value has error
     * then it will not cache the value.
     * @param invoker    service
     * @param invocation invocation.
     * @return Cache returned value if found by the underlying cache store. If cache miss it will call target method.
     * @throws RpcException
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (cacheFactory != null && ConfigUtils.isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), CACHE_KEY))) {
            Cache cache = cacheFactory.getCache(invoker.getUrl(), invocation);
            if (cache != null) {
                // 使用参数拼接成key
                String key = StringUtils.toArgumentString(invocation.getArguments());
                // xjh-从缓存中获取
                Object value = cache.get(key);
                // xjh-如果值不为空，则直接返回
                if (value != null) {
                    if (value instanceof ValueWrapper) {
                        return AsyncRpcResult.newDefaultAsyncResult(((ValueWrapper) value).get(), invocation);
                    } else {
                        return AsyncRpcResult.newDefaultAsyncResult(value, invocation);
                    }
                }
                Result result = invoker.invoke(invocation);
                if (!result.hasException()) {
                    cache.put(key, new ValueWrapper(result.getValue()));
                }
                return result;
            }
        }
        return invoker.invoke(invocation);
    }

    /**
     * Cache value wrapper.
     */
    static class ValueWrapper implements Serializable {

        private static final long serialVersionUID = -1777337318019193256L;

        private final Object value;

        public ValueWrapper (Object value) {
            this.value = value;
        }

        public Object get() {
            return this.value;
        }
    }
}
