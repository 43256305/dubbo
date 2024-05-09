package org.apache.dubbo.demo.learning.spi.dubbo.api;

import org.apache.dubbo.common.extension.SPI;

/**
 * @ClassName org.apache.dubbo.demo.learning.spi.dubbo.api.Robot
 * @Description TODO
 * @Author xiejiehui
 * @Date 2024/5/9 17:15
 */
@SPI
public interface Robot {
    void sayHello();
}