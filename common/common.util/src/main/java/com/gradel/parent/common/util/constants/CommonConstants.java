package com.gradel.parent.common.util.constants;


import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 应用常量
 */
public interface CommonConstants {
    /**
     * UTF-8编码
     */
    String UTF8 = "UTF-8";

    /**
     * UTF-8编码字符集
     */
    Charset UTF8_CHARSET = Charset.forName(UTF8);

    /**
     * 空的byte数组
     */
    byte[] EMPTY_BYTES = new byte[0];

    /**
     * 空List集合
     */
    List EMPTY_LIST = Collections.EMPTY_LIST;

    /**
     * 空Map集合
     */
    Map EMPTY_MAP = Collections.EMPTY_MAP;

    /**
     * 空Set集合
     */
    Set EMPTY_SET = Collections.EMPTY_SET;

    /**
     * empty object
     */
    Object EMPTY_OBJECT = new Object();

    String EMPTY_STRING = "";

    int DEFAULT_POLL_TIMEOUT = 100;


    String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    char DEFAULT_DELIMITER_CHAR = ',';

    String DEFAULT_DELIMITER_STRING = ",";

    char DEFAULT_ASTERISK_CHAR = '*';

    String[] EMPTY_STRING_ARRAY = new String[0];
}
