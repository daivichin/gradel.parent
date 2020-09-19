package com.gradel.parent.common.util.util;

import java.util.HashMap;
import java.util.Map;

/**
 * RadixEncoder
 *
 * @Date 2019/8/27 下午1:03
 * @Author sdeven
 */
public class RadixEncoder {
    private final static Map<Character, Integer> map = new HashMap<>();
    private final static Character[] digits = new Character[55];
    static {
        map.put('P', 0);
        map.put('Q', 1);
        map.put('R', 2);
        map.put('S', 3);
        map.put('T', 4);
        map.put('6', 5);
        map.put('7', 6);
        map.put('8', 7);
        map.put('9', 8);
        map.put('c', 9);
        map.put('d', 10);
        map.put('e', 11);
        map.put('f', 12);
        map.put('g', 13);
        map.put('h', 14);
        map.put('j', 15);
        map.put('k', 16);
        map.put('m', 17);
        map.put('n', 18);
        map.put('U', 19);
        map.put('V', 20);
        map.put('W', 21);
        map.put('X', 22);
        map.put('Y', 23);
        map.put('Z', 24);
        map.put('C', 25);
        map.put('D', 26);
        map.put('E', 27);
        map.put('F', 28);
        map.put('G', 29);
        map.put('H', 30);
        map.put('u', 31);
        map.put('v', 32);
        map.put('w', 33);
        map.put('x', 34);
        map.put('y', 35);
        map.put('z', 36);
        map.put('A', 37);
        map.put('B', 38);
        map.put('2', 39);
        map.put('3', 40);
        map.put('4', 41);
        map.put('5', 42);
        map.put('J', 43);
        map.put('K', 44);
        map.put('L', 45);
        map.put('M', 46);
        map.put('N', 47);
        map.put('p', 48);
        map.put('q', 49);
        map.put('r', 50);
        map.put('s', 51);
        map.put('t', 52);
        map.put('a', 53);
        map.put('b', 54);

        for (Map.Entry<Character, Integer> e : map.entrySet()) {
            digits[e.getValue()] = e.getKey();
        }
    }

    public static String encode(long id, int radix) {
        if (radix < 2 || radix > 55)
            radix = 10;
        if (radix == 10)
            return String.valueOf(id);
        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (id < 0);

        if (!negative) {
            id = -id;
        }

        while (id <= -radix) {
            buf[charPos--] = digits[(int)(-(id % radix))];
            id = id / radix;
        }
        buf[charPos] = digits[(int)(-id)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (65 - charPos));
    }

    public static Long decode(String code, int radix) {
        char[] charArray = code.toCharArray();
        long result = 0L;
        for (int pos = 0; pos < charArray.length; ++pos) {
            char ch = charArray[charArray.length - pos - 1];
            int number = map.get(ch);

            result += number * Math.pow(radix, pos);
        }

        return result;

    }
}
