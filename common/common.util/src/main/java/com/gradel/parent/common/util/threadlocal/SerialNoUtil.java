package com.gradel.parent.common.util.threadlocal;

import com.gradel.parent.common.util.api.enums.AppNameBase;
import com.gradel.parent.common.util.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 流水号生成工具类

 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/23
 * @Description:
 */
@Slf4j
public class SerialNoUtil {
    private static Object lock = new Object();
    private static int index = 0;

    /**
     * <p>
     * generateSerialNo 流水号生成方法<br/>
     * 生成规则：<br/>
     * 2位 平台编码<br>
     * 12位 IP,如172026007023表示172.26.7.23<br>
     * 15位 时间:yyMMddHHmmssSSS <br>
     * 3位 索引号(000-999) <br>
     * 共32位 如03192168095206150202092529959648
     * </p>
     *
     * @param appName 应用名称
     * @return
     */
    public static String generateSerialNo(AppNameBase appName) {
        StringBuilder stringBuilder = new StringBuilder(38);
        stringBuilder.append(appName.getCodeNumber());
        stringBuilder.append(LocalHostUtil.getIpCode());
        stringBuilder.append(DateUtil.formatDate(new Date(), DateUtil.YYMMDDHHMMSSSSS));
        String indexV = "000";
        synchronized (lock) {
            index++;
            if (index > 999) {
                index = 0;
            }
            indexV = "" + index;
        }
        if (indexV.length() < 2) {
            indexV = "00" + indexV;
        }else if (indexV.length() < 3) {
            indexV = "0" + indexV;
        }
        stringBuilder.append(indexV);
        return stringBuilder.toString();
    }


}