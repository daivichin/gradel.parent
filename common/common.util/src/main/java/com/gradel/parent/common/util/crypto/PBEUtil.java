package com.gradel.parent.common.util.crypto;

import com.gradel.parent.common.util.constants.CommonConstants;
import com.gradel.parent.common.util.exception.CommonRuntimeException;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/8/30
 * @Description:
 */
@Slf4j
public class PBEUtil {
    /**
     * 加/解密用的口令
     */
    static final char[] SECRET_PASSWD = "e6953ec7057662c70d9cabf4186a77a6".toCharArray();

    static final byte[] secretSalt = {-87, -101, -56, 50, 86, 53, -29, 3};

    static final int secretIterations = 1000;

    static final String ENCRYPTED_MESSAGE_CHARSET = "US-ASCII";


    /**
     * base64解密<br/>
     * 需要jdk的rt.jar支持
     *
     * @param data
     * @return
     */
    public static byte[] base64Decode(String data) {
        try {
            return Base64.getDecoder().decode(data.getBytes(ENCRYPTED_MESSAGE_CHARSET));
        } catch (Exception e) {
            log.error("[{}] Base64 decode string[{}] Exception:{}", SerialNo.getSerialNo(), data, ExceptionUtil.getAsString(e));
            throw new CommonRuntimeException(e);
        }
    }

    /**
     * base64方式加密<br/>
     * 需要jdk的rt.jar支持
     *
     * @param data
     * @return
     */
    public static String base64Encode(byte[] data) {
        try {
            return new String(Base64.getEncoder().encode(data), ENCRYPTED_MESSAGE_CHARSET);
        } catch (Exception e) {
            log.error("[{}] Base64 encode byte array Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new CommonRuntimeException(e);
        }
    }

    /**
     * 对base64处理过的数据解密为原文
     *
     * @param data
     * @return
     */
    public static String decrypt(String data) {
        byte[] tmp = base64Decode(data);
        byte[] dd = decrypt(tmp);
        try {
            return new String(dd, CommonConstants.UTF8);
        } catch (UnsupportedEncodingException e) {
            log.error("[{}] byte convert to String Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return null;
    }

    /**
     * 基于口令的解密方法
     *
     * @param data
     * @return
     */
    public static byte[] decrypt(byte[] data) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(SECRET_PASSWD);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede", "SunJCE");
            Key passwdKey = keyFactory.generateSecret(keySpec);
            PBEParameterSpec paramSpec = new PBEParameterSpec(secretSalt, secretIterations);

            Cipher cip = Cipher.getInstance("PBEWithSHA1AndDESede", "SunJCE");
            cip.init(Cipher.DECRYPT_MODE, passwdKey, paramSpec);
            //数据加密
            return cip.doFinal(data);
        } catch (Exception ex) {
            log.error("[{}] Decrypt byte array Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(ex));
            throw new CommonRuntimeException(ex);
        }
    }

    /**
     * 对原文进行加密,加密的数据为base64格式.
     *
     * @param src
     * @return
     */
    public static String encrypt(String src) {
        byte[] ed = null;
        try {
            ed = encrypt(src.getBytes(CommonConstants.UTF8));
        } catch (UnsupportedEncodingException e) {
            log.error("[{}] String convert to bytes Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        if (ed != null) {
            return base64Encode(ed);
        } else {
            return null;
        }
    }

    /**
     * 基于口令的加密方法
     *
     * @param src
     * @return
     */
    public static byte[] encrypt(byte[] src) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(SECRET_PASSWD);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede", "SunJCE");
            Key passwdKey = keyFactory.generateSecret(keySpec);
            PBEParameterSpec paramSpec = new PBEParameterSpec(secretSalt, secretIterations);

            Cipher cip = Cipher.getInstance("PBEWithSHA1AndDESede", "SunJCE");
            cip.init(Cipher.ENCRYPT_MODE, passwdKey, paramSpec);
            //数据加密
            return cip.doFinal(src);
        } catch (Exception ex) {
            log.error("[{}] Encrypt byte array Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(ex));
            throw new CommonRuntimeException(ex);
        }
    }

    public static void main(String[] ars) {
//        System.out.println(passwordDecrypter.encrypt("Aa123456"));
//        System.out.println(passwordDecrypter.encrypt("123456"));

        System.out.println(encrypt("Aa123456"));
        System.out.println(decrypt(encrypt("Aa123456")));
//        System.out.println(passwordDecrypter.decrypt(passwordDecrypter.encrypt("123456")));

    }
}
