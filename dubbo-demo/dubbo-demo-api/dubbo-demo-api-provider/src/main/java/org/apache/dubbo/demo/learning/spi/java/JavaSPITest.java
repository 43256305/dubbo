package org.apache.dubbo.demo.learning.spi.java;

import org.apache.dubbo.demo.learning.spi.java.api.Robot;

import java.util.ServiceLoader;

/**
 * @ClassName JavaSPITes
 * @Description TODO
 * @Author xiejiehui
 * @Date 2024/5/9 17:18
 */
public class JavaSPITest {

    public static void main(String[] args) {
        // java原生的spi使用 参考：https://cn.dubbo.apache.org/zh-cn/docsv2.7/dev/source/dubbo-spi/
        // 如mysql的Driver就是使用了java原生的spi机制，可以参考connect-j包下面的META-INF文件夹下面的文件
        ServiceLoader<Robot> serviceLoader = ServiceLoader.load(Robot.class);
        System.out.println("Java SPI");
        serviceLoader.forEach(Robot::sayHello);
    }

}