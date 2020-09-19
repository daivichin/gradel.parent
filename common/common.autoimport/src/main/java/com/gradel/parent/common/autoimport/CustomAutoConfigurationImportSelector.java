package com.gradel.parent.common.autoimport;

import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 解决手动import 类加载顺序问题，请参考
 1. https://gooroo.io/GoorooTHINK/Article/17466/Lessons-Learned-Writing-Spring-Boot-Auto-Configurations/29652#.W_ZwdFUzYdV%20%20https://blog.csdn.net/isea533/article/details/53975720
 2. https://www.cnblogs.com/yszzu/p/10002281.html
 不在同一个包下:
 ==>> spring.factories 中定义了autoConfig 会被加载

 ==>> spring.factories 没有定义autoConfig， 不会被加载
 　　如果其他@Configuration类@Import了这个autoConfig, 会被加载

 　　其他@Configuration中@Autowire了spring.factories生成的@Bean,  导致提前初始化
 同一包名下：
 ==>> 不管spring.factories中有没有定义，扫描到后立即加载
 排序算法 org.springframework.boot.autoconfigure.AutoConfigurationSorter#doSortByAfterAnnotation

 ＠import

 Auto configurations should never be included via @ComponentScan because ordering cannot be guaranteed.
 Auto configurations should live in a different package to avoid being accidentally picked up by @ComponentScan.
 Auto configurations should be declared in a META-INF/spring.factories and should NOT be subject to @ComponentScan as mentioned above.
 @Ordered does not apply to @Configuration classes since Spring Boot 1.3.
 Use @AutoConfigureOrder, @AutoConfigureBefore, and @AutoConfigureAfter to order auto configurations for Spring Boot 1.3 or greater.
 Avoid using @ConditionalOnX annotations outside of auto-configurations. @ConditionalOnX annotations are sensitive to ordering and ordering cannot be guaranteed with just @Configuration classes.


 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/16 下午9:48
 * @Description: 解决手动＠import 类加载顺序问题
 */
public class CustomAutoConfigurationImportSelector extends AutoConfigurationImportSelector {
    @Override
    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        return asList(attributes, "value");
    }

    @Override
    protected Class<?> getAnnotationClass() {
        return CustomImportAutoConfiguration.class;
    }

    @Override
    protected Set<String> getExclusions(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        return Collections.EMPTY_SET;
    }
}
