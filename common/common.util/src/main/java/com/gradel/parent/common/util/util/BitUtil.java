package com.gradel.parent.common.util.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/10/22
 * @Description: bit 工具类
 */
@Slf4j
public abstract class BitUtil {

    public final static int BYTE_LEN = 8;
    public final static int INT_LEN = 32;
    public final static int INT_EXIST = 0x0001;


    /**
     * 生成一个指定N位为1的int值
     *
     * @param startBit 开始位，从0开始
     * @param count    总共多少位
     */
    public static int generateIntWithBit(int startBit, int count) {
        if (startBit < 0 || count < 0) {
            throw new IllegalArgumentException("params must not less than 0");
        }

        if ((startBit + count) > 32) {
            throw new IndexOutOfBoundsException("len must not more than " + INT_LEN);
        }

        int val = 0;
        while (count > 0) {
            val |= INT_EXIST << startBit;
            startBit++;
            count--;
        }
        return val;
    }

    /**
     * 获取当前整形二进制位中第一次出现1的下标，从0开始
     *
     * @param value
     */
    public static int getBitExistStartIndex(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("params must not less than 0");
        }

        if (value == 0) {
            return -1;
        }
        int startIndex = 0;

        while (startIndex < INT_LEN) {
            int currentVal = INT_EXIST << startIndex;
            if (currentVal == (value & (INT_EXIST << startIndex))) {
                break;
            }
            startIndex++;
        }

        if(startIndex >= INT_LEN){
            return -1;
        }else {
            return startIndex;
        }
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    public static void main(String[] args) {
        byte b = 0x1; // 0011 0101
        // 输出 [0, 0, 1, 1, 0, 1, 0, 1]
        System.out.println(Arrays.toString(getBooleanArray(b)));
        // 输出 00110101
        System.out.println(byteToBit(b));
        // JDK自带的方法，会忽略前面的 0
        System.out.println(Integer.toBinaryString(0x35));
        System.out.println(generateIntWithBit(2, 1));
        System.out.println(Integer.toBinaryString(generateIntWithBit(1, 1)));
        System.out.println(Integer.toBinaryString(generateIntWithBit(2, 1)));
        System.out.println(Integer.toBinaryString(generateIntWithBit(3, 1)));
        System.out.println(Integer.toBinaryString(generateIntWithBit(4, 1)));
        System.out.println(Integer.toBinaryString(generateIntWithBit(5, 1)));

        System.out.println(Integer.toBinaryString(generateIntWithBit(4, 2)));
        System.out.println(Integer.toBinaryString(generateIntWithBit(5, 2)));
    }
}
