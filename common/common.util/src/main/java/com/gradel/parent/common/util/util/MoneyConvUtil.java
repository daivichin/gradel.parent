package com.gradel.parent.common.util.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 *  分元单位转换
 */
public class MoneyConvUtil {
    /**
     * 单位转换:分转元
     *
     * @param money
     *            金额
     * @return 返回元值
     * @throws Exception
     */
    public static String divide(String money) {
        BigDecimal bigMoney = new BigDecimal(money);
        BigDecimal feedRate = new BigDecimal("100");
        DecimalFormat df = new DecimalFormat("###################0.00");
        String returnMoney = df.format(bigMoney.divide(feedRate, 2, BigDecimal.ROUND_HALF_UP));
        return returnMoney;
    }
    /**
     * 单位转换:分转元
     * @param money
     * @return 返回元值
     */
    public static BigDecimal divide2(String money) {
        BigDecimal bigMoney = new BigDecimal(money);
        BigDecimal feedRate = new BigDecimal("100");
        bigMoney = bigMoney.divide(feedRate, 2, BigDecimal.ROUND_HALF_UP);
        return bigMoney;
    }
    /**
     *  单位转换:分转元
     * @param money
     * @return 返回元值
     */
    public static String divide(BigDecimal money) {
        BigDecimal feedRate = new BigDecimal("100");
        DecimalFormat df = new DecimalFormat("###################0.00");
        String returnMoney = df.format(money.divide(feedRate, 2, BigDecimal.ROUND_HALF_UP));
        return returnMoney;
    }

    /**
     * 单位转换:元转分
     *
     * @param money 金额
     * @return 返回分值
     * @throws Exception
     */
    public static String multiply(String money) {
        BigDecimal bigMoney = new BigDecimal(money);
        BigDecimal feedRate = new BigDecimal(100);
        DecimalFormat df = new DecimalFormat("###################0");
        String returnMoney = df.format(bigMoney.multiply(feedRate).setScale(0, BigDecimal.ROUND_HALF_UP));
        return returnMoney;
    }
    /**
     * 单位转换:元转分
     * @param money
     * @return 返回分值
     */
    public static String multiply(BigDecimal money) {
        BigDecimal feedRate = new BigDecimal(100);
        DecimalFormat df = new DecimalFormat("###################0");
        String returnMoney = df.format(money.multiply(feedRate).setScale(0, BigDecimal.ROUND_HALF_UP));
        return returnMoney;
    }

    /**
     * 单位转换:元转分
     * @param money
     * @return 返回分值
     */
    public static BigDecimal multiply2BD(BigDecimal money) {
        BigDecimal feedRate = new BigDecimal("100");
        BigDecimal result = money.multiply(feedRate).setScale(0, BigDecimal.ROUND_HALF_UP);
        return result;
    }

    /**
     *  单位转换:分转元
     * @param money
     * @return 返回元值
     */
    public static BigDecimal divide2BD(BigDecimal money) {
        BigDecimal feedRate = new BigDecimal("100");
        BigDecimal result = money.divide(feedRate, 2, BigDecimal.ROUND_HALF_UP);
        return result;
    }
}
