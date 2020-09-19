package com.gradel.parent.common.util.threadlocal;
import com.gradel.parent.common.util.api.enums.AppNameBase;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/23
 * @Description: 流水号
 */
public class SerialNo {

    private static final InheritableThreadLocal<String> SERIALNO_THREAD_LOCAL = new InheritableThreadLocal<String>();

    public static String getSerialNo(){
        return SERIALNO_THREAD_LOCAL.get();
    }

    public static void setSerialNo(String serialNo){
        SERIALNO_THREAD_LOCAL.set(serialNo);
    }

    public static String init(AppNameBase appName){
        String serialNo = SerialNoUtil.generateSerialNo(appName);
        SERIALNO_THREAD_LOCAL.set(serialNo);
        return serialNo;
    }

    public static void clear(){
        SERIALNO_THREAD_LOCAL.remove();
    }
}
