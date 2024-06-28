package org.apache.dubbo.demo.consumer.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Random;

/**
 * @ClassName JarVersionConsumerFilter
 * @Description consumer过滤器
 * @Author xiejiehui
 * @Date 2024/6/28 16:35
 */
@Activate(group = {CommonConstants.CONSUMER}, order = -1)
public class JarVersionConsumerFilter implements Filter {

    private static final String JAR_VERSION_NAME_KEY = "dubbo.jar.version";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        invocation.setAttachment(JAR_VERSION_NAME_KEY, getJarVersion());
        return invoker.invoke(invocation);
    }

    private String getJarVersion() {
        Random rand = new Random();
        // 生成随机数1或2
        int randomNumber = rand.nextInt(2) + 1;
        System.out.println(randomNumber);
        return "version" + randomNumber;

    }


}
