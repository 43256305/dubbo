package org.apache.dubbo.demo.learning.adaptive.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.demo.learning.adaptive.api.PersonService;

/**
 * @ClassName EnglandPersonServiceImpl
 * @Description TODO
 * @Author xiejiehui
 * @Date 2024/6/19 11:26
 */
public class EnglandPersonServiceImpl implements PersonService {
    @Override
    public String queryCountry(URL url) {
        System.out.println("英国人");
        return "英国人";
    }

}
