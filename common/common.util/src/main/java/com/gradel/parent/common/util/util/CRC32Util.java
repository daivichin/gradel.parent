package com.gradel.parent.common.util.util;

import java.util.zip.CRC32;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/01/02 下午5:14
 */
public class CRC32Util {
    public static long crc32Code(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }
}
