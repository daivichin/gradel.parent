package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/5
 * @Description:
 */
@Slf4j
public class UTF8Encoder {

    public UTF8Encoder() {
    }

    public static byte[][] encodeMany(String... strs) {
        byte[][] many = new byte[strs.length][];

        for (int i = 0; i < strs.length; ++i) {
            many[i] = encode(strs[i]);
        }

        return many;
    }

    public static byte[] encode(String str) {
        if (str != null) {
            try {
                return str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("encode the string [{}] Some Exception Occur:[{}]", str, ExceptionUtil.getAsString(e));
            }
        }
        return CommonConstants.EMPTY_BYTES;
    }

    public static String decode(byte[] data) {
        if (data != null && data.length != 0) {
            try {
                return new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("decode the bytes, Some Exception Occur:[{}]", ExceptionUtil.getAsString(e));
            }
        }
        return CommonConstants.EMPTY_STRING;
    }
}
