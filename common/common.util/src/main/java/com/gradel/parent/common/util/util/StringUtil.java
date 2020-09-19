package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.constants.CommonConstants;
import com.gradel.parent.common.util.pattern.RegPattern;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-12
 * @Description:处理字符串
 */
public class StringUtil extends StringUtils {
    //默认替换支付
    public static final String DEFAULT_CHAR = "*";
    //证件号前面保留位数
    public static final int CERT_NO_PREX_NUM = 4;
    //证件号后面保留位数
    public static final int CERT_NO_POST_NUM = 3;
    //手机号前面保留位数
    public static final int MOBILE_PREX_NUM = 3;
    //手机号后面保留位数
    public static final int MOBILE_POST_NUM = 4;
    //银行卡号前面保留位数
    public static final int BANKCARD_NO_PREX_NUM = 6;
    //银行卡号后面保留位数
    public static final int BANKCARD_NO_POST_NUM = 4;

    public static final Pattern REG_DECIMALS = Pattern.compile(RegPattern.REG_DECIMALS);

    public static boolean isEmpty(Object obj) {
        return obj == null || "".equals(obj);
    }

    public static String replaceDefaultPwd(String source) {
        return replaceDefaultChar(source, 0, 0);
    }

    /**
     * 替换默认的证件号
     *
     * @param source
     * @return
     */
    public static String replaceDefaultCertNo(String source) {
        return replaceChar(source, CERT_NO_PREX_NUM, CERT_NO_POST_NUM, DEFAULT_CHAR);
    }


    /**
     * 替换默认的手机号
     *
     * @param source
     * @return
     */
    public static String replaceDefaultMobile(String source) {
        return replaceChar(source, MOBILE_PREX_NUM, MOBILE_POST_NUM, DEFAULT_CHAR);
    }

    /**
     * 替换默认的银行卡号
     *
     * @param source
     * @return
     */
    public static String replaceDefaultBankCardNo(String source) {
        return replaceChar(source, BANKCARD_NO_PREX_NUM, BANKCARD_NO_POST_NUM, DEFAULT_CHAR);
    }


    /**
     * 替换默认字符
     *
     * @param source
     * @param prexNum
     * @param postNum
     * @return
     */
    public static String replaceDefaultChar(String source, int prexNum, int postNum) {
        return replaceChar(source, prexNum, postNum, DEFAULT_CHAR);
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkDecimals(String decimals) {
        return REG_DECIMALS.matcher(decimals).matches();
    }

    /**
     * 替换字符
     *
     * @param source
     * @param prexNum  从1开始
     * @param postNum  从1开始
     * @param toString
     * @return
     */
    public static String replaceChar(String source, int prexNum, int postNum, String toString) {
        if (StringUtils.isNotBlank(source)) {
            if (prexNum < 0) {
                prexNum = 0;
            }
            if (postNum < 0) {
                postNum = 0;
            }
            if (source.length() > (prexNum + postNum)) {
                StringBuilder newStrBu = new StringBuilder(source.length());
                newStrBu.append(source.substring(0, prexNum));
                int start = prexNum;
                int end = source.length() - postNum;
                for (int i = start; i < end; i++) {
                    newStrBu.append(toString);
                }
                newStrBu.append(source.substring(end));
                return newStrBu.toString();
            }
            return source;
        }
        return CommonConstants.EMPTY_STRING;
    }

    /**
     * 判断一个对象是否是空，如果是空则返回true，否则返回false
     *
     * @param obj
     * @return boolean
     */
    public static boolean isNull(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof String) {
            if ("".equals(((String) obj).trim()) || "null".equals(((String) obj).trim().toLowerCase())
                    || "NULL".equals(((String) obj).trim().toLowerCase())) {
                return true;
            } else {
                return false;
            }
        } else if (obj instanceof List) {
            if (((List<?>) obj).isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else if (obj instanceof HashSet) {
            if (((HashSet<?>) obj).isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else if (obj instanceof HashMap) {
            if (((HashMap<?, ?>) obj).isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else if (obj instanceof Set) {
            if (((Set<?>) obj).isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /*public static String getUUID1() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getUUID2() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
    }*/

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        // 去掉"-"符号
        return new StringBuilder(32).append(uuid.substring(0, 8)).append(uuid.substring(9, 13))
                .append(uuid.substring(14, 18)).append(uuid.substring(19, 23))
                .append(uuid.substring(24)).toString();
    }


    /**
     * 判断输入的字符串是否是UUID(包含32位和36位的验证)
     *
     * @param uuid
     * @return
     */
    public static boolean isUUID(String uuid) {
        if (isNotBlank(uuid)) {
            int len = uuid.length();
            if (len == 32) {
                StringBuilder sb = new StringBuilder(64);
                sb.append(uuid.substring(0, 8));
                sb.append("-");
                sb.append(uuid.substring(8, 12));
                sb.append("-");
                sb.append(uuid.substring(12, 16));
                sb.append("-");
                sb.append(uuid.substring(16, 20));
                sb.append("-");
                sb.append(uuid.substring(20));
                return isUUIDFormatString(sb.toString());
            } else if (len == 36) {
                return isUUIDFormatString(uuid);
            }
        }
        return false;
    }

    private static boolean isUUIDFormatString(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断输入的字符串是否是UUID(包含32位和36位的验证)
     *
     * @param uuid
     * @return
     */
    public static boolean isNotUUID(String uuid) {
        return !isUUID(uuid);
    }

    /**
     * 获取随机生成4位数
     *
     * @return
     */
    public static String rendomFour() {
        StringBuilder stringBuilder = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            stringBuilder.append(RandomUtil.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 获取随机生成5位数
     *
     * @return
     */
    public static String randomFive() {
        StringBuilder stringBuilder = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(RandomUtil.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 获取随机生成15位数
     *
     * @return
     */
    public static String randomFifteen() {
        StringBuilder stringBuilder = new StringBuilder(15);
        for (int i = 0; i < 15; i++) {
            stringBuilder.append(RandomUtil.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 获取随机生成6位数
     *
     * @return
     */
    public static String rendomSix() {
        StringBuilder stringBuilder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(RandomUtil.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 拼接
     *
     * @param source 源字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return
     */
    public static String append(String source, String prefix, String suffix) {
        if (StringUtil.isNotBlank(source)) {
            StringBuilder stringBuilder = new StringBuilder();
            if (!source.startsWith(prefix)) {
                stringBuilder.append(prefix);
            }
            stringBuilder.append(source);
            if (!source.endsWith(suffix)) {
                stringBuilder.append(suffix);
            }
            return stringBuilder.toString();
        }
        return source;
    }

    /**
     * 拼接 到 前缀和后缀
     *
     * @param source 源字符串
     * @param str    前缀
     * @return
     */
    public static String appendToStartAndEnd(String source, String str) {
        return append(source, str, str);
    }

    /**
     * 去头尾
     *
     * @param source 源字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return
     */
    public static String trim(String source, String prefix, String suffix) {
        if (StringUtil.isNotBlank(source)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(source);
            if (source.startsWith(prefix)) {
                stringBuilder.deleteCharAt(0);
            }
            if (source.endsWith(suffix)) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            return stringBuilder.toString();
        }
        return source;
    }

    /**
     * 去头尾
     *
     * @param source  源字符串
     * @param trimStr 前缀
     * @return
     */
    public static String trim(String source, String trimStr) {
        return trim(source, trimStr, trimStr);
    }

    /**
     * 描述： 字符串特殊字符转义处理
     *
     * @author sdeven.chen.dongwei@gmail.com
     */
    public static String escapeString(String content) {
        content = JavaScriptUtil.javaScriptEscape(content);
        return content;
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(
            String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection collection) {
        if (collection == null) {
            return null;
        }
        return (String[]) collection.toArray(new String[collection.size()]);
    }

    public static String clean(String in) {
        String out = in;

        if (in != null) {
            out = in.trim();
            if (out.equals(CommonConstants.EMPTY_STRING)) {
                out = null;
            }
        }

        return out;
    }

    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String[] split(String aLine, char delimiter, char beginQuoteChar, char endQuoteChar,
                                 boolean retainQuotes, boolean trimTokens) {
        String line = clean(aLine);
        if (line == null) {
            return null;
        }

        List<String> tokens = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);
            if (c == beginQuoteChar) {
                // this gets complex... the quote may end a quoted block, or escape another quote.
                // do a 1-char lookahead:
                if (inQuotes  // we are in quotes, therefore there can be escaped quotes in here.
                        && line.length() > (i + 1)  // there is indeed another character to check.
                        && line.charAt(i + 1) == beginQuoteChar) { // ..and that char. is a quote also.
                    // we have two quote chars in a row == one quote char, so consume them both and
                    // put one on the token. we do *not* exit the quoted text.
                    sb.append(line.charAt(i + 1));
                    i++;
                } else {
                    inQuotes = !inQuotes;
                    if (retainQuotes) {
                        sb.append(c);
                    }
                }
            } else if (c == endQuoteChar) {
                inQuotes = !inQuotes;
                if (retainQuotes) {
                    sb.append(c);
                }
            } else if (c == delimiter && !inQuotes) {
                String s = sb.toString();
                if (trimTokens) {
                    s = s.trim();
                }
                tokens.add(s);
                sb = new StringBuilder(); // start work on next token
            } else {
                sb.append(c);
            }
        }
        String s = sb.toString();
        if (trimTokens) {
            s = s.trim();
        }
        tokens.add(s);
        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * transaction-manager > transactionManager
     *
     * @param attributeName
     * @return
     */
    public static String attributeNameToPropertyName(String attributeName) {
        if (isEmpty(attributeName) || !attributeName.contains("-")) {
            return attributeName;
        }
        char[] chars = attributeName.toCharArray();
        char[] result = new char[chars.length - 1]; // not completely accurate but good guess
        int currPos = 0;
        boolean upperCaseNext = false;
        for (char c : chars) {
            if (c == '-') {
                upperCaseNext = true;
            } else if (upperCaseNext) {
                result[currPos++] = Character.toUpperCase(c);
                upperCaseNext = false;
            } else {
                result[currPos++] = c;
            }
        }
        return new String(result, 0, currPos);
    }

    /**
     * transactionManager > transaction-manager
     *
     * @param propertyName
     * @return
     */
    public static String propertyNameToAttributeName(String propertyName) {
        if (isEmpty(propertyName)) {
            return propertyName;
        }
        char[] chars = propertyName.trim().toCharArray();
        if (chars.length > 0) {
            char[] result = new char[chars.length + chars.length / 2]; // not completely accurate but good guess
            int currPos = 0;
            boolean currUpperCase;
            boolean preUpperCase = false;
            for (char c : chars) {
                currUpperCase = Character.isUpperCase(c);
                if (currUpperCase) {
                    if (currPos != 0 && !preUpperCase && '-' != result[currPos - 1]) {
                        result[currPos++] = '-';
                    }
                    result[currPos++] = Character.toLowerCase(c);
                } else {
                    result[currPos++] = c;
                }
                preUpperCase = currUpperCase;
            }
            return new String(result, 0, currPos);
        }
        return propertyName;
    }

    public static boolean isNotEmptyOrNvlObj(Object str) {
        return !(str == null || str.toString().trim().length() == 0 || "null".equalsIgnoreCase(str.toString()));
    }
    
    public static void main(String[] args) {

        System.out.println(replaceDefaultMobile("13560280810"));
        System.out.println(replaceDefaultCertNo("44088119900110355X"));

        System.out.println(trim(",fsfds,", ",", ","));

        int count = 100000;
        long start = System.currentTimeMillis();
        int len = count;
        while (len-- > 0) {
            getUUID();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);




        /*start = System.currentTimeMillis();
        len = count;
        while (len-- > 0) {
            getUUID1();
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);

        start = System.currentTimeMillis();
        len = count;
        while (len-- > 0) {
            getUUID2();
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);*/

        start = System.currentTimeMillis();
        len = count;
        while (len-- > 0) {
            getUUID();
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);

    }
}
