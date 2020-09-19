package com.gradel.parent.common.util.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ParseProcess;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.util.IOUtils;
import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;
import com.gradel.parent.common.util.exception.SystemException;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * json转换集合类型
 *
 * @author: sdeven.chen.dongwei@gmail.com
 */
@Slf4j
public class JsonUtil {

    @Getter
    private static final SerializerFeature[] defaultFeatures = new SerializerFeature[]{
            //在fastjson中，会自动检测循环引用，并且输出为fastjson专有的引用表示格式。但这个不能被其他JSON库识别，也不能被浏览器识别，所以fastjson提供了关闭循环引用检测的功能。
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.QuoteFieldNames,//输出key时是否使用双引号,默认为true
            SerializerFeature.WriteMapNullValue,//是否输出值为null的字段,默认为false

            SerializerFeature.WriteBigDecimalAsPlain,

            //屏蔽掉, 开启后则 SerializerFeature.WriteClassName 不生效，redis序列化会有问题 @see MyGenericFastjsonRedisSerializer
            //SerializerFeature.NotWriteRootClassName,

            //支持数据输出xss漏洞安全转义,不全局打开,建议每个业务按需设置
            //SerializerFeature.BrowserSecure,
    };

    private static FastJsonConfig fastJsonConfig;

    //******************************* toJson or writeJson ***********************************//
    /**
     * 对象序列化
     *
     * @param obj 需要序列化的对象
     * @return 返回json字符串
     */
    public static String toJson(Object obj) {
        return toJson(obj, getFastJsonConfig().getSerializeConfig(), getFastJsonConfig().getSerializeFilters(),
                getFastJsonConfig().getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, getFastJsonConfig().getSerializerFeatures());
    }

    public static Object toJSON(Object javaObject) {
        return toJSON(javaObject, getFastJsonConfig().getSerializeConfig());
    }

    public static Object toJSON(Object javaObject, SerializeConfig config) {
        try {
            return JSON.toJSON(javaObject, config);
        } catch (Throwable e) {
            log.error("[{}] Object convert to JSON Some Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
        }
    }

    /**
     * 格式化输出json
     * @param object
     * @param prettyFormat 是否格式化
     * @return
     */
    public static String toJson(Object object, boolean prettyFormat) {
        if (!prettyFormat) {
            return toJson(object);
        }
        return toJson(object, SerializerFeature.PrettyFormat);
    }

    public static String toJson(Object object, SerializerFeature... features) {
        return toJson(object, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    /**
     * @param object
     * @param dateFormat
     * @param features
     * @return
     */
    public static String toJsonWithDateFormat(Object object, String dateFormat,
                                              SerializerFeature... features) {
        return toJson(object, getFastJsonConfig().getSerializeConfig(), getFastJsonConfig().getSerializeFilters(), dateFormat, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    public static String toJson(Object object, int defaultFeatures, SerializerFeature... features) {
        try {
            return JSON.toJSONString(object, defaultFeatures, features);
        } catch (Throwable e) {
            log.error("[{}] Object convert to JSON Some Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
        }
    }

    public static String toJson(Object object, SerializeFilter[] filters, SerializerFeature... features) {
        return toJson(object, getFastJsonConfig().getSerializeConfig(), filters, getFastJsonConfig().getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    public static String toJsonWithDateFormat(Object object, String dateFormat, SerializeFilter[] filters, SerializerFeature... features) {
        return toJson(object, getFastJsonConfig().getSerializeConfig(), filters, dateFormat, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    public static String toJson(Object object, SerializeConfig config, SerializerFeature... features) {
        return toJson(object, config, getFastJsonConfig().getSerializeFilters(), features);
    }

    public static String toJsonWithDateFormat(Object object, String dateFormat, SerializeConfig config, SerializerFeature... features) {
        return toJsonWithDateFormat(object, dateFormat, config, getFastJsonConfig().getSerializeFilters(), features);
    }

    public static String toJson(Object object, //
                                      SerializeConfig config, //
                                      SerializeFilter filter, //
                                      SerializerFeature... features) {
        return toJson(object, config, new SerializeFilter[] {filter}, getFastJsonConfig().getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    public static String toJson(Object object, //
                                              SerializeConfig config, //
                                              SerializeFilter[] filters, //
                                              SerializerFeature... features) {
        return toJson(object, config, filters, getFastJsonConfig().getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    public static String toJsonWithDateFormat(Object object, //
                                String dateFormat,
                                SerializeConfig config, //
                                SerializeFilter[] filters, //
                                SerializerFeature... features) {
        return toJson(object, config, filters, dateFormat, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    public static String toJson(Object object, //
                                SerializeConfig config, //
                                SerializeFilter[] filters, //
                                String dateFormat, //
                                int defaultFeatures, //
                                SerializerFeature... features) {
        try {
            return JSON.toJSONString(object, config, filters, dateFormat, defaultFeatures, features);
        } catch (Throwable e) {
            log.error("[{}] Object convert to JSON Some Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
        }
    }

    public static byte[] toJsonBytes(Object object, SerializerFeature... features) {
        return toJsonBytes(object, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    /**
     * @since 1.2.11
     */
    public static byte[] toJsonBytes(Object object, int defaultFeatures, SerializerFeature... features) {
        return toJsonBytes(object, getFastJsonConfig().getSerializeConfig(), defaultFeatures, features);
    }

    public static byte[] toJsonBytes(Object object, SerializeConfig config, SerializerFeature... features) {
        return toJsonBytes(object, config, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    /**
     * @since 1.2.11
     */
    public static byte[] toJsonBytes(Object object, SerializeConfig config, int defaultFeatures, SerializerFeature... features) {
        try {
            return JSON.toJSONBytes(object, config, defaultFeatures, features);
        } catch (Throwable e) {
            log.error("[{}] Object convert to JSON Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
        }
    }


    public static void writeJson(Writer writer, Object object, SerializerFeature... features) {
        writeJson(writer, object, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    /**
     * @since 1.2.11
     */
    public static void writeJson(Writer writer, Object object, int defaultFeatures, SerializerFeature... features) {
        try {
            JSON.writeJSONString(writer, object, defaultFeatures, features);
        } catch (Throwable e) {
            log.error("[{}] Object convert to JSON Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
        }
    }

    /**
     * write object as json to OutputStream
     * @param os output stream
     * @param object
     * @param features serializer features
     * @since 1.2.11
     * @throws IOException
     */
    public static final int writeJson(OutputStream os, //
                                            Object object, //
                                            SerializerFeature... features) {
        return writeJson(os, object, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    /**
     * @since 1.2.11
     */
    public static final int writeJson(OutputStream os, //
                                            Object object, //
                                            int defaultFeatures, //
                                            SerializerFeature... features) {
        return writeJson(os,  //
                IOUtils.UTF8, //
                object, //
                getFastJsonConfig().getSerializeConfig(), //
                getFastJsonConfig().getSerializeFilters(), //
                getFastJsonConfig().getDateFormat(), //
                defaultFeatures, //
                features);
    }

    public static final int writeJsonWithDateFormat(OutputStream os, //
                                            Object object, //
                                            String dataFormat, //
                                            SerializerFeature... features) throws IOException {
        return writeJson(os, //
                IOUtils.UTF8, //
                object, //
                getFastJsonConfig().getSerializeConfig(), //
                getFastJsonConfig().getSerializeFilters(), //
                dataFormat, //
                JSON.DEFAULT_GENERATE_FEATURE, //
                features);
    }

    public static final int writeJson(OutputStream os, //
                                            Charset charset, //
                                            Object object, //
                                            SerializeConfig config, //
                                            SerializeFilter[] filters, //
                                            String dateFormat, //
                                            int defaultFeatures, //
                                            SerializerFeature... features){
        try {
            return JSON.writeJSONString(os, charset, object, config, filters, dateFormat, defaultFeatures, features);
        } catch (Throwable e) {
            log.error("[{}] Object convert to JSON Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
        }
    }
    
    
    
    
    
    
    
    
    
    //************************ parse **********************************//
    public static Object parse(String text) {
        return parse(text, JSON.DEFAULT_PARSER_FEATURE);
    }

    public static Object parse(String text, int features) {
        try {
            return JSON.parse(text, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static Object parse(String text, Feature... features) {
        try {
            return JSON.parse(text, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static Object parse(byte[] input, Feature... features) {
        try {
            return JSON.parse(input, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }










    //************************ parse JSONObject **********************************//


    public static JSONObject parseObject(String text, Feature... features) {
        return (JSONObject) parse(text, features);
    }

    public static JSONObject parseObject(String text) {
        Object obj = parse(text);
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }

        try {
            return (JSONObject) JSON.toJSON(obj);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T parseObject(String text, TypeReference<T> type, Feature... features) {
        try {
            return JSON.parseObject(text, type, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T parseObject(String json, Class<T> clazz, Feature... features) {
        try {
            return JSON.parseObject(json, clazz, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T parseObject(String json, Type type, Feature... features) {
        try {
            return JSON.parseObject(json, type, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T parseObject(String input, Type clazz, int featureValues, Feature... features) {
        try {
            return JSON.parseObject(input, clazz, featureValues, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T parseObject(String input, Type clazz, ParserConfig config, Feature... features) {
        try {
            return JSON.parseObject(input, clazz, config, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T parseObject(String input, Type clazz, ParserConfig config) {
        try {
            return JSON.parseObject(input, clazz, config, getFastJsonConfig().getFeatures());
        } catch (Throwable e) {
            log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T parseObject(byte[] bytes, Type clazz, Feature... features) {
        try {
            return JSON.parseObject(bytes, clazz, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    /**
     *
     * @param json
     * @param keyType
     * @param valueType
     * @param <K>
     * @param <V>
     * @see https://github.com/alibaba/fastjson/wiki/TypeReference
     *
     * 例如：// 可以这样使用 String json = "{1:{name:\"ddd\"},2:{name:\"zzz\"}}";
     * @return
     */
    public static <K, V> Map<K, V> parseToMap(String json,
                                              Class<K> keyType,
                                              Class<V> valueType) {
        return parseObject(json,
                new TypeReference<Map<K, V>>(keyType, valueType) {
                });
    }








    //************************ parse Object **********************************//


    public static <T> T fromJson(String text, Class<T> clazz) {
        try {
            return JSON.parseObject(text, clazz);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    /**
     * <pre>
     * String jsonStr = "[{\"id\":1001,\"name\":\"Jobs\"}]";
     * List&lt;Model&gt; models = JSON.parseObject(jsonStr, new TypeReference&lt;List&lt;Model&gt;&gt;() {});
     * </pre>
     * @param text json string
     * @param type type refernce
     * @param features
     * @return
     */
    public static <T> T fromJson(String text, TypeReference<T> type, Feature... features) {
        try {
            return JSON.parseObject(text, type, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz, Feature... features) {
        try {
            return JSON.parseObject(json, clazz, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T fromJson(String text, Class<T> clazz, ParseProcess processor, Feature... features) {
        try {
            return JSON.parseObject(text, clazz, processor, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T fromJson(String json, Type type, Feature... features) {
        try {
            return JSON.parseObject(json, type, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T fromJson(String input, Type clazz, ParseProcess processor, Feature... features) {
        try {
            return JSON.parseObject(input, clazz, processor, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T fromJson(String input, Type clazz, int featureValues, Feature... features) {
        try {
            return JSON.parseObject(input, clazz, featureValues, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    /**
     * @since 1.2.11
     */
    public static <T> T fromJson(String input, Type clazz, ParserConfig config, Feature... features) {
        try {
            return JSON.parseObject(input, clazz, config, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> T fromJson(String input, Type clazz, ParserConfig config, int featureValues,
                                    Feature... features) {
        try {
            return JSON.parseObject(input, clazz, config, featureValues, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }


    public static <T> T fromJson(byte[] bytes, Type clazz, Feature... features) {
        try {
            return JSON.parseObject(bytes, clazz, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    /**
     * @since 1.2.11
     */
    public static <T> T fromJson(InputStream is, //
                                    Type type, //
                                    Feature... features) {
        try {
            return JSON.parseObject(is, type, features);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }







    //************************ parse Array **********************************//

    public static JSONArray parseArray(String text) {
        try {
            return JSON.parseArray(text);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Array Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        try {
            return JSON.parseArray(text, clazz);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Array Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static List<Object> parseArray(String text, Type[] types) {
        try {
            return JSON.parseArray(text, types);
        } catch (Throwable e) {
            log.error("[{}] JSON convert to Array Exception Occur:[{}]", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
        }
    }

    public static FastJsonConfig getFastJsonConfig() {
        if(fastJsonConfig == null){
            synchronized (JsonUtil.class){
                if(fastJsonConfig == null){
                    FastJsonConfigBean fastJsonConfigBean = new FastJsonConfigBean();
                    fastJsonConfigBean.setEnableDefault(true);
                    fastJsonConfigBean.setEnableJsonUtil(false);
                    try {
                        fastJsonConfigBean.afterPropertiesSet();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    fastJsonConfig = fastJsonConfigBean;
                }
            }
        }
        return fastJsonConfig;
    }

    public void setFastJsonConfig(FastJsonConfig fastJsonConfig) {
        JsonUtil.fastJsonConfig = fastJsonConfig;
    }

    public static void fastJsonConfig(FastJsonConfig fastJsonConfig) {
        JsonUtil.fastJsonConfig = fastJsonConfig;
    }
}
