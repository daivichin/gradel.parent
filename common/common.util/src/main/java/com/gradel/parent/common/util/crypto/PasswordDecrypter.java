package com.gradel.parent.common.util.crypto;

import com.gradel.parent.common.util.exception.SystemException;
import com.gradel.parent.common.util.threadlocal.SerialNo;
import com.gradel.parent.common.util.api.crypto.StringEncDecryption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordDecrypter implements StringEncDecryption {

    private DESInstance encrypter;

    public PasswordDecrypter(){
        this.encrypter = new DESInstance();
    }

    public PasswordDecrypter(String key){
        this.encrypter = new DESInstance(key);
    }

    @Override
    public String decrypt(String encryptedString) throws SystemException {
        try {
            String decrypted = this.encrypter.decrypt(encryptedString);
            return decrypted;
        } catch (Exception e) {
        	log.error("encryptedString[{}] ##[{}][DES decrypt ERROR] Error enountered during decryption - check decryption algorithm & key match that used for encryption", encryptedString, SerialNo.getSerialNo());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public String encrypt(String message) throws SystemException {
        try {
            String ecrypted = this.encrypter.encrypt(message);
            return ecrypted;
        } catch (Exception e) {
            log.error("[{}][DES encrypt ERROR] Error enountered during ecryption - check ecryption algorithm", SerialNo.getSerialNo());
            throw new SystemException(e.getMessage());
        }
    }

    public static void main(String[] ars) {
        PasswordDecrypter passwordDecrypter = new PasswordDecrypter("$)(#@Si&^%.Bu+=!");

        String data = "root";
        System.out.println(data + "==" + passwordDecrypter.encrypt(data));
        data = "Aa123456";
        System.out.println(data + "==" + passwordDecrypter.encrypt(data));
        data = "123456";
        System.out.println(data + "==" + passwordDecrypter.encrypt(data));
        data = "sibu_wesale";
        System.out.println(data + "==" + passwordDecrypter.encrypt(data));



        data = "GRbUEjlFfVrEv1nYK+DfdA==";
        System.out.println(data + "==" + passwordDecrypter.decrypt(data));

        data = "Z/r8C+GcIw6eTEQp5T8tUWSjKGl68kov";
        System.out.println(data + "==" + passwordDecrypter.decrypt(data));

        /*long start = System.currentTimeMillis();
        int i = 1000;
        while (i-- > 0){
            Token token = Token.getToken(StringUtil.getUUID(), 51254, new Date(), Token.DEFAULT_VERSION);
            passwordDecrypter.encrypt(token.toString());
        }
        System.out.println(System.currentTimeMillis() - start);*/
    }
}
