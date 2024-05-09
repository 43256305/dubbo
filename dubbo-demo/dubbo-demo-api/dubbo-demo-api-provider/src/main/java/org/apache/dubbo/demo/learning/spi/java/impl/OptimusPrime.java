package org.apache.dubbo.demo.learning.spi.java.impl;

import org.apache.dubbo.demo.learning.spi.java.api.Robot;

/**
 * @ClassName OptimusPrime
 * @Description TODO
 * @Author xiejiehui
 * @Date 2024/5/9 17:16
 */
public class OptimusPrime implements Robot {

    @Override
    public void sayHello() {
        System.out.println("Hello, I am Optimus Prime.");
    }
}