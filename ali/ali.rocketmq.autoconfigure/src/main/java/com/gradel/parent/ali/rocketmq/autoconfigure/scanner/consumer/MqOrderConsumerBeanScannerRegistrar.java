package com.gradel.parent.ali.rocketmq.autoconfigure.scanner.consumer;

import com.gradel.parent.ali.rocketmq.autoconfigure.ClassPathConsumerScanner;
import com.gradel.parent.ali.rocketmq.autoconfigure.MqOrderConsumer;
import com.gradel.parent.ali.rocketmq.autoconfigure.RocketMqConsumerBeanPostProcessor;
import com.gradel.parent.ali.rocketmq.autoconfigure.bean.RocketMqOrderConsumerPostBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import  com.gradel.parent.common.util.util.CollectionUtil;

import java.util.List;
import java.util.Map;

/**
 * 有序消费者
 * @see RocketMqConsumerBeanPostProcessor
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/02 下午10:15
 */
@Deprecated
@Slf4j
public class MqOrderConsumerBeanScannerRegistrar
        implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        log.debug("Searching for Mq order Consumer with @MqOrderConsumer");

        ClassPathConsumerScanner scanner = new ClassPathConsumerScanner(registry);

        try {
            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }

            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
            if (log.isDebugEnabled()) {
                packages.forEach(pkg -> log.debug("Using auto-configuration base package '{}'", pkg));
            }

            scanner.setAnnotationClass(MqOrderConsumer.class);
//                scanner.setMarkerInterface(RocketMqMessageListener.class);
            scanner.registerFilters();
            Map<String, List<RuntimeBeanReference>> groupBeanMap = scanner.doScanAndGet(StringUtils.toStringArray(packages));
            if (CollectionUtil.isNotEmpty(groupBeanMap)) {
                registerRocketMqOrderConsumerBeans(groupBeanMap, registry);
            }

        } catch (IllegalStateException ex) {
            log.debug("Could not determine auto-configuration package, automatic MqOrderConsumer scanning disabled.", ex);
        }
    }

    private void registerRocketMqOrderConsumerBeans(Map<String, List<RuntimeBeanReference>> groupBeanMap, BeanDefinitionRegistry registry) {
        for (String beanName : groupBeanMap.keySet()) {
            List<RuntimeBeanReference> subscriptionList = groupBeanMap.get(beanName);
            RootBeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClass(RocketMqOrderConsumerPostBean.class);
//                beanDefinition.setLazyInit(true);
            beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue("beanName", beanName));
            beanDefinition.getPropertyValues().addPropertyValue("subscriptionList", subscriptionList);
//                beanDefinition.getPropertyValues().addPropertyValue("properties", createMqConsumerProDefinition(StringUtil.propertyNameToAttributeName(beanName) + ".properties"));
            beanDefinition.setInitMethodName("start");
            beanDefinition.setDestroyMethodName("shutdown");
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private RootBeanDefinition createMqConsumerProDefinition(String consumerProperties) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(PropertiesFactoryBean.class);
        beanDefinition.getPropertyValues().addPropertyValue("locations", consumerProperties);
//            PropertiesFactoryBean beanReferenceMap = null;
//            Properties properties = PropertiesLoaderUtils.loadProperties(new EncodedResource(new ClassPathResource(consumerProperties)));
        return beanDefinition;

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}