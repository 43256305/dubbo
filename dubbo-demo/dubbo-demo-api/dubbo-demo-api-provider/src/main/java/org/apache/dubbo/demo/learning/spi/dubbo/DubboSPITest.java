package org.apache.dubbo.demo.learning.spi.dubbo;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.demo.learning.spi.dubbo.api.Robot;

/**
 * @ClassName DubboSPITest
 * @Description TODO
 * @Author xiejiehui
 * @Date 2024/5/9 17:24
 */
public class DubboSPITest {

    public static void main(String[] args) {
        // dubbo使用的spi机制 需要在Robot上注解@SPI。相比java的spi机制，能够指定加载的实现类
        // java spi机制会加载配置文件中的所有类，并将这些类全部实例话，而dubbo的spi机制虽然也会加载所有的实现类，但是只会实例化指定的实现类
        ExtensionLoader<Robot> extensionLoader =
                ExtensionLoader.getExtensionLoader(Robot.class);
        Robot optimusPrime = extensionLoader.getExtension("optimusPrime");
        optimusPrime.sayHello();
        Robot bumblebee = extensionLoader.getExtension("bumblebee");
        bumblebee.sayHello();
    }

}