package org.apache.dubbo.demo.learning.adaptive.api;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * @Description TODO
 * @Author xiejiehui
 * @Date 2024/6/19 11:23
 */
@SPI
public interface PersonService {

    @Adaptive
    String queryCountry(URL url);

}
