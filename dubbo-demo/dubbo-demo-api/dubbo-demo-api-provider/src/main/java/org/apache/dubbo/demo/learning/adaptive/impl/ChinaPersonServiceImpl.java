package org.apache.dubbo.demo.learning.adaptive.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.demo.learning.adaptive.api.PersonService;

/**
 * @ClassName ChinaPersonServiceImpl
 * @Description TODO
 * @Author xiejiehui
 * @Date 2024/6/19 11:24
 */
public class ChinaPersonServiceImpl implements PersonService {
    @Override
    public String queryCountry(URL url) {
        System.out.println("中国人");
        return "中国人";
    }

}
