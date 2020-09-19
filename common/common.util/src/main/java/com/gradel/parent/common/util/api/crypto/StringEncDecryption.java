package com.gradel.parent.common.util.api.crypto;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2018/12/29 下午4:05
 * @Description: String 加解密接口
 */
public interface StringEncDecryption {

    String decrypt(String encryptedString);

    String encrypt(String message);
}
