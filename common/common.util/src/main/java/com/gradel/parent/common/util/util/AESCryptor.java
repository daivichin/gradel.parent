package com.gradel.parent.common.util.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

public class AESCryptor {
	
	public static final AESCryptor cryptor = new AESCryptor();

    public static boolean initialized = false;
    
    public static final String ALGORITHM_AES = "AES";
    public static final String PROVIDER_BC= "BC";
    public static final String TRANSFORMATION_AES_CBC_PKCS7PADDING = "AES/CBC/PKCS7Padding";
    public static final String TRANSFORMATION_AES_ECB_PKCS7PADDING = "AES/ECB/PKCS7Padding";

    public String decryptToString(byte[] content, byte[] keyByte, byte[] ivByte) {
        return new String(
        		this.decrypt(content, keyByte, ivByte, TRANSFORMATION_AES_CBC_PKCS7PADDING), StandardCharsets.UTF_8);
    }
    public String decryptToString_ECB(byte[] content, byte[] keyByte, byte[] ivByte) {
        return new String(
                this.decrypt(content, keyByte, ivByte, TRANSFORMATION_AES_ECB_PKCS7PADDING), StandardCharsets.UTF_8);
    }

    /**
     * AES解密
     * @param content 密文
     * @return 解密后的字符串
     */
    public byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte, String transformation) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance(transformation, PROVIDER_BC);
            Key sKeySpec = new SecretKeySpec(keyByte, ALGORITHM_AES);

            if (null == ivByte) {
                cipher.init(Cipher.DECRYPT_MODE, sKeySpec);// 初始化
            }
            else {
                cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化
            }
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void initialize(){
        if (!initialized) {
        	Security.addProvider(new BouncyCastleProvider());
            initialized = true;
        }
    }
    //生成iv
    public AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance(ALGORITHM_AES);
        params.init(new IvParameterSpec(iv));
        return params;
    }
}
