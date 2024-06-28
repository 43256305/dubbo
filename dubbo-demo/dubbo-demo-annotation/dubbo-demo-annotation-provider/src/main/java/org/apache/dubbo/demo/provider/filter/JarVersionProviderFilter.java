package org.apache.dubbo.demo.provider.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName JarVersionProviderFilter
 * @Description TODO
 * @Author xiejiehui
 * @Date 2024/6/28 16:42
 */
@Activate(group = {CommonConstants.PROVIDER}, order = -1)
public class JarVersionProviderFilter implements Filter {

    private static final String JAR_VERSION_NAME_KEY = "dubbo.jar.version";
    private static final Map<String, AtomicLong> versionState = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    public JarVersionProviderFilter() { // 启动定时任务

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {

            for (Map.Entry<String, AtomicLong> entry : versionState.entrySet()) {

                System.out.println("版本：" + entry.getKey() + ":" + entry.getValue().get()); // 打印日志

            }

        }, 10, 10, TimeUnit.SECONDS);

    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String version = invocation.getAttachment(JAR_VERSION_NAME_KEY);
        if (version != null) {
            AtomicLong count = versionState.computeIfAbsent(version, v -> new AtomicLong(0L));
            count.getAndIncrement(); // 递增该版本的统计值
        }
        return invoker.invoke(invocation);
    }
}
