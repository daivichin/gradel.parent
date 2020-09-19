package com.gradel.parent.common.util.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {
    public static String encode(String str, boolean upperCase) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] result = md.digest();
            StringBuilder sb = new StringBuilder(32);
            for (byte b : result) {
                int val = b & 0xff;
                if (val <= 0xf) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            if (upperCase) {
                return sb.toString().toUpperCase();
            }
            else {
                return sb.toString().toLowerCase();
            }
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
}
