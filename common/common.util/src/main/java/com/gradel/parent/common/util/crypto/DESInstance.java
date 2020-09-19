package com.gradel.parent.common.util.crypto;

import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.exception.SystemException;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/8/30
 * @Description:
 */
@Slf4j
public class DESInstance {

    private final static String DES = "DES";

    private static final String MESSAGE_CHARSET = "UTF-8";

    private static final String ENCRYPTED_MESSAGE_CHARSET = "US-ASCII";

    private static String defaultKey = "$)(#@Si&^%.Bu+=!";

    private DESKeySpec desKeySpec;

    private IvParameterSpec iv;

    /**
     * 默认构造方法，使用默认密钥
     */
    public DESInstance() {
        this(defaultKey);
    }

    /**
     * 指定密钥构造方法
     *
     * @param key 指定的密钥
     * @throws Exception
     */
    public DESInstance(String key) {
        // 创建一个DESKeySpec对象
        try {
            desKeySpec = new DESKeySpec(getBitsSufBefore(key));
            iv = new IvParameterSpec(getBitsSufAfter(key));
        } catch (InvalidKeyException e) {
            log.error("[{}]DESInstance Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.error("[{}]DESInstance Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * 加密字节数组
     *
     * @param arrB 需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */
    public byte[] encrypt(byte[] arrB) throws Exception {
        // 创建一个DESKeySpec对象
//        DESKeySpec desKeySpec = new DESKeySpec(getBitsSufBefore(key));
//        IvParameterSpec iv = new IvParameterSpec(getBitsSufAfter(key));
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        // 真正开始解密操作
        return cipher.doFinal(arrB);
    }

    /**
     * 进行加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String encrypt(String data) throws Exception {
        byte[] bt = encrypt(data.getBytes());
        byte[] encode = Base64.getEncoder().encode(bt);
        String strs = new String(encode, ENCRYPTED_MESSAGE_CHARSET);
        return strs;
    }

    /**
     * 解密字节数组
     *
     * @param arrB 需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public byte[] decrypt(byte[] arrB) throws Exception {
        // 创建一个DESKeySpec对象
//        DESKeySpec desKeySpec = new DESKeySpec(getBitsSufBefore(key));
//        IvParameterSpec iv = new IvParameterSpec(getBitsSufAfter(key));
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        // 真正开始解密操作
        return cipher.doFinal(arrB);
    }

    /**
     * 进行解密
     *
     * @param data
     * @throws Exception
     */
    public String decrypt(String data) throws Exception {
        if (data == null){
            return null;
        }else{
            byte[] buf = Base64.getDecoder().decode(data.getBytes(ENCRYPTED_MESSAGE_CHARSET));
            byte[] bt = decrypt(buf);
            return new String(bt);
        }
    }

    private static byte[] getBitsSufBefore(String key) throws UnsupportedEncodingException {
        return getBits(key, true);
    }

    private static byte[] getBitsSufAfter(String key) throws UnsupportedEncodingException {
        return getBits(key, false);
    }

    /**
     * 根据key 获取前8位或者后8位
     * 将字符串格式化为>=8字节长度的字节数组，不足补0
     *
     * @param key
     * @param before
     * @return
     */
    private static byte[] getBits(String key, boolean before) throws UnsupportedEncodingException {
        byte[] bt = key.getBytes(MESSAGE_CHARSET);
        byte[] btnew = new byte[8];
        if (bt.length < btnew.length) {
            //小于8位则补0
            System.arraycopy(bt, 0, btnew, 0, bt.length);
        } else if (before) {
            //前8位
            System.arraycopy(bt, 0, btnew, 0, btnew.length);
        } else {
            //后8位
            System.arraycopy(bt, bt.length - btnew.length, btnew, 0, btnew.length);
        }
        return btnew;
    }

    public static void main(String[] args) throws Exception {

        DESInstance desInstance = new DESInstance("$)(#@Si&^%.Bu+=!");

        String data = "123456";
        System.out.println(data);
//        String encryptStr = desInstance.encrypt(data);
        System.out.println(desInstance.encrypt("Aa123456"));

        System.out.println(desInstance.encrypt("sibu_wesale"));
        System.out.println(desInstance.encrypt("emIFhtPc6tKiniwfvNC6"));
        System.out.println(desInstance.decrypt("Z6QwQOVzfy56Ho8p0oXIrLhMql6tXvB6E8Zdrw/GQrQZfFFmDckOJCnjeqt/QZBHYbqJry5QD2lTzmjSzJ9ptw=="));


        long start = System.currentTimeMillis();
        int i = 10;
        while (i-- > 0) {
//            Token token = Token.getToken("00000FC-8AAB-7B67438DBD91", 51254, new Date(), Token.DEFAULT_VERSION);
//            desInstance.encrypt(token.toString());
//            desInstance.decrypt("Z6QwQOVzfy56Ho8p0oXIrLhMql6tXvB6E8Zdrw/GQrQZfFFmDckOJCnjeqt/QZBHYbqJry5QD2lTzmjSzJ9ptw==");
        }
        System.out.println(System.currentTimeMillis() - start);

//        Token token = Token.getToken("00000FC-8AAB-7B67438DBD91", 51254, new Date(), Token.DEFAULT_VERSION);

        /*String data = token.toString();
        System.out.println(data);
        String encryptStr = desInstance.encrypt(data);
        System.out.println(encryptStr);
        System.out.println(desInstance.decrypt("6k+DNshLI1xKXsfH/5xiQxWMHLSmX/V71aqrzPpXONQVxL1VfYm9hwChzGTygC4C+i9keQORod+683MxpXQ0EQ=="));

        token = Token.getToken("00000FC-8AAB-weqweqew", 44444444, new Date(), Token.DEFAULT_VERSION);
        data = token.toString();
        encryptStr = desInstance.encrypt(data);
        System.out.println(encryptStr);
        System.out.println(desInstance.decrypt(encryptStr));

        token = Token.getToken("00000FC-8AAB-qqqqqqqqqq", 111111, new Date(), Token.DEFAULT_VERSION);
        data = token.toString();
        encryptStr = desInstance.encrypt(data);
        System.out.println(encryptStr);
        System.out.println(desInstance.decrypt(encryptStr));

        token = Token.getToken("00000FC-8AAB-3333333333", 222222222, new Date(), Token.DEFAULT_VERSION);
        data = token.toString();
        encryptStr = desInstance.encrypt(data);
        System.out.println(encryptStr);
        System.out.println(desInstance.decrypt(encryptStr));*/
    }
}