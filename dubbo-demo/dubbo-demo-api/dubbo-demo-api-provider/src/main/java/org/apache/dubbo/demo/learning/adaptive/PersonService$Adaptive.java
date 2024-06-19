package org.apache.dubbo.demo.learning.adaptive;

/**
 * @ClassName PersonService$Adaptive
 * @Description 生成的代码
 * @Author xiejiehui
 * @Date 2024/6/19 13:51
 */
import org.apache.dubbo.common.extension.ExtensionLoader;

public class PersonService$Adaptive implements org.apache.dubbo.demo.learning.adaptive.api.PersonService {
    public java.lang.String queryCountry(org.apache.dubbo.common.URL arg0) {
        if (arg0 == null) throw new IllegalArgumentException("url == null");
        org.apache.dubbo.common.URL url = arg0;
        // xjh-这里代码的生成参考此方法：org.apache.dubbo.common.extension.AdaptiveClassCodeGenerator.generateExtNameAssignment，获取url的参数person.service值
        String extName = url.getParameter("person.service");
        if (extName == null)
            throw new IllegalStateException("Failed to get extension (org.apache.dubbo.demo.learning.adaptive.api.PersonService) name from url (" + url.toString() + ") use keys([person.service])");
        // xjh-根据extName来获取真正的实现类
        org.apache.dubbo.demo.learning.adaptive.api.PersonService extension = (org.apache.dubbo.demo.learning.adaptive.api.PersonService) ExtensionLoader.getExtensionLoader(org.apache.dubbo.demo.learning.adaptive.api.PersonService.class).getExtension(extName);
        return extension.queryCountry(arg0);
    }
}
