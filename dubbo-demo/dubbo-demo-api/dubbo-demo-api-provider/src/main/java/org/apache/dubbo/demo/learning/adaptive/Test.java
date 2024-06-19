package org.apache.dubbo.demo.learning.adaptive;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.demo.learning.adaptive.api.PersonService;

/**
 * @ClassName Test
 * @Description dubbo的@Adaptive注解配合SPI机制的使用：https://www.cnblogs.com/wtzbk/p/16656309.html
 * https://learn.lianglianglee.com/%e4%b8%93%e6%a0%8f/Dubbo%e6%ba%90%e7%a0%81%e8%a7%a3%e8%af%bb%e4%b8%8e%e5%ae%9e%e6%88%98-%e5%ae%8c/04%20%20Dubbo%20SPI%20%e7%b2%be%e6%9e%90%ef%bc%8c%e6%8e%a5%e5%8f%a3%e5%ae%9e%e7%8e%b0%e4%b8%a4%e6%9e%81%e5%8f%8d%e8%bd%ac%ef%bc%88%e4%b8%8b%ef%bc%89.md
 * @Author xiejiehui
 * @Date 2024/6/19 11:26
 */
public class Test {

    public static void main(String[] args) {
        URL url = URL.valueOf("dubbo://192.168.0.101:20880?person.service=china");
        // 根据传入的url的person.service=china，根据spi机制/@Adaptive机制获取对应的实现类，即ChinaPersonServiceImpl
        PersonService service = ExtensionLoader.getExtensionLoader(PersonService.class)
                .getAdaptiveExtension();
        service.queryCountry(url);
    }

}
