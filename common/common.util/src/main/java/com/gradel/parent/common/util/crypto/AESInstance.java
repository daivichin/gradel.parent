package com.gradel.parent.common.util.crypto;

import com.gradel.parent.common.util.exception.CommonRuntimeException;
import com.gradel.parent.common.util.util.ExceptionUtil;
import com.gradel.parent.common.util.exception.SystemException;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2017/1/12
 * @Description:AES加解密
 */
@Slf4j
public class AESInstance {

    private final static String AES = "AES";

    private static final String MESSAGE_CHARSET = "UTF-8";

    private SecretKeySpec secretKeySpec;

    private IvParameterSpec iv;

    /**
     * 指定密钥构造方法
     *
     * @param key 指定的密钥
     * @throws Exception
     */
    public AESInstance(String sessionKey, String key) {
        try {
            // 创建一个AESKeySpec对象
            secretKeySpec = new SecretKeySpec(base64Decode(sessionKey), AES);
            iv = new IvParameterSpec(base64Decode(key));
        } catch (Throwable e) {
            log.error("[{}]AESInstance Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new SystemException(e.getMessage());
        }
    }

    private byte[] base64Decode(String value) throws UnsupportedEncodingException {
        return Base64.getDecoder().decode(value.getBytes(MESSAGE_CHARSET));
    }

    /**
     * 加密字节数组
     *
     * @param arrB 需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */
    public byte[] encrypt(byte[] arrB) {
        try {
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            // 真正开始解密操作
            return cipher.doFinal(arrB);
        } catch (Exception ex) {
            log.error("[{}] AES Encrypt byte array Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(ex));
            throw new CommonRuntimeException(ex);
        }
    }

    /**
     * 进行加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public String encrypt(String data) {
        try {
            byte[] bt = encrypt(data.getBytes(MESSAGE_CHARSET));
            byte[] encode = Base64.getEncoder().encode(bt);
            return new String(encode, MESSAGE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            log.error("[{}] AES Encrypt, String convert to bytes Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            throw new CommonRuntimeException(e);
        }
    }

    /**
     * 解密字节数组
     *
     * @param arrB 需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public byte[] decrypt(byte[] arrB) {
        try {
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            // 真正开始解密操作
            return cipher.doFinal(arrB);
        } catch (Exception ex) {
            log.error("[{}] AES Decrypt byte array Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(ex));
            throw new CommonRuntimeException(ex);
        }
    }

    /**
     * 进行解密
     *
     * @param data
     * @throws Exception
     */
    public String decrypt(String data) {
        if (data == null) {
            return null;
        } else {
            byte[] buf;
            try {
                buf = Base64.getDecoder().decode(data.getBytes(MESSAGE_CHARSET));
            } catch (UnsupportedEncodingException e) {
                log.error("[{}] AES Decrypt, String convert to bytes Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
                throw new CommonRuntimeException(e);
            }
            byte[] bt = decrypt(buf);
            return new String(bt);
        }
    }

    public static void main(String[] args) throws Exception {
        String iv = "r7BXXKkLb8qrSNn05n0qiA==";
        String sessionKey = "tiihtNczf5v6AKRyjwEUhQ==";
        String encryptedData = "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZMQmRzooG2xrDcvSnxIMXFufNstNGTyaGS9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+3hVbJSRgv+4lGOETKUQz6OYStslQ142dNCuabNPGBzlooOmB231qMM85d2/fV6ChevvXvQP8Hkue1poOFtnEtpyxVLW1zAo6/1Xx1COxFvrc2d7UL/lmHInNlxuacJXwu0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn/Hz7saL8xz+W//FRAUid1OksQaQx4CMs8LOddcQhULW4ucetDf96JcR3g0gfRK4PC7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns/8wR2SiRS7MNACwTyrGvt9ts8p12PKFdlqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYVoKlaRv85IfVunYzO0IKXsyl7JCUjCpoG20f0a04COwfneQAGGwd5oa+T8yO5hzuyDb/XcxxmK01EpqOyuxINew==";
//        String encryptedData = "U2FsdGVkX19uv9HKLSg5l2BdsJIm3yd2Bu2N4jdR/XOpbFjG6uQYjh6tMrLglV/EfbKu4d8TF/oUuLfPck4KtMom1Xl8/LtDqo5Bnk/MZW3p1kutD5gxrlz9tYcBEJ9zJoECNZj+Zdd1fygMvPE+lRKyXWh/6Wcus/Tqh9j9n70j/AIA/oaQth5va9vPuxU6D/bUsedUclGVttTaBFvbBIHbVIJF1VodUsGyCdTkBx+l176w0qrXchkRDD/ImcdY96c993SJsxd36n5YJ+w+QP+1xHlEYD18T0Coo104w0Nwvk5GS6ZdNFQMD8ZmCStWge8ZaD+31nw7aBVq8qYhCUGMYpJX2Nt9lSUmx8y2fe6rWE6NroLQp6Sx0iiwCB2VfWdCfywwSqMPGc6Oha7JXDJlBd5+k4IOYc5cOFTj/FKnUasJ+7NZC4LUZNLwIzAqNWCOaaJfacSoh2/R9hlxzTjK2HLgKdaUphCref5aQl+ol4S9rqBX/bX8Cp+ZYXQRjOBzTt374Udowv4EuVNCSEXXCNSHW329qATI4ouH43Q=";

        AESInstance aes = new AESInstance(sessionKey, iv);
        String json = aes.decrypt(encryptedData);
        System.out.println("解密-json=" + json);
        System.out.println("加密-json=" + aes.encrypt(json));
    }
}