package com.gradel.parent.component.nodeflow.groovy;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.util.Map;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017/9/28 20:23
 * @Description: 读取接口结果文件测试groovy脚本执行
 */
public class ExprSupportTest {

    public static void main(String[] args) throws Exception {
       String nfJson = IOUtils.toString(ExprSupportTest.class.getResourceAsStream("/result_ivs.json"));
        Map<String, Object> row = (Map<String, Object>) new Gson().fromJson(nfJson, Map.class);
       System.out.println(ExprSupport.parseExpr("ivsDetail[1].code == 'PHONE_Mismatch' || ivsDetail[1].code == 'PHONE_History_NegativeList' || ivsDetail[1].code == 'PHONE_Missing'", row));

    }
}
