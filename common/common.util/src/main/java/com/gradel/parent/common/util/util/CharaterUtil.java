package com.gradel.parent.common.util.util;


import com.gradel.parent.common.util.constants.CommonConstants;

/**
 * 描述：生成固定长度的数字与字母混合随机数
 */
public class CharaterUtil {

    /**
     * 描述：生成固定长度的数字与字母混合随机数
     */
    public static String genFixLengthRandom(int length) {
        String str = CommonConstants.CHARACTERS;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int number = RandomUtil.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}