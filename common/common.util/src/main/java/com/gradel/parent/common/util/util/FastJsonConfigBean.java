package com.gradel.parent.common.util.util;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017-08-17
 */
public class FastJsonConfigBean extends FastJsonConfig implements InitializingBean {

    @Setter
    private Boolean enableJsonUtil;

    @Setter
    private Boolean enableDefault = true;

    @Getter
    private Set<SerializerFeature> serializerFeatureSet = Collections.EMPTY_SET;

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<SerializerFeature> newSerializerFeatureSet = new HashSet<>();
        newSerializerFeatureSet.addAll(serializerFeatureSet);

        if (enableDefault) {
            newSerializerFeatureSet.addAll(Arrays.asList(JsonUtil.getDefaultFeatures()));
        }

        setSerializerFeatures(newSerializerFeatureSet.toArray(new SerializerFeature[newSerializerFeatureSet.size()]));

        if (enableJsonUtil == null || enableJsonUtil) {
            JsonUtil.fastJsonConfig(this);
        }
    }

    public void addSerializerFeature(SerializerFeature... serializerFeatures) {
        if (serializerFeatureSet == (Set<SerializerFeature>) Collections.EMPTY_SET) {
            serializerFeatureSet = new HashSet<>();
        }
        for (SerializerFeature serializerFeature : serializerFeatures) {
            serializerFeatureSet.add(serializerFeature);
        }
    }


}
