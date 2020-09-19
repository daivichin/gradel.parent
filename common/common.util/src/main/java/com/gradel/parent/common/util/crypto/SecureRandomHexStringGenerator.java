package com.gradel.parent.common.util.crypto;


import com.gradel.parent.common.util.util.HexUtil;
import com.gradel.parent.common.util.api.crypto.RandomNumberGenerator;

import java.security.SecureRandom;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/6/29
 * @Description:
 */
public class SecureRandomHexStringGenerator implements RandomNumberGenerator {

    protected static final int DEFAULT_NEXT_BYTES_SIZE = 16; //16 bytes == 128 bits (a common number in crypto)

    private int defaultNextBytesSize;
    private SecureRandom secureRandom;

    public SecureRandomHexStringGenerator() {
        this.defaultNextBytesSize = DEFAULT_NEXT_BYTES_SIZE;
        this.secureRandom = new SecureRandom();
    }

    public void setSeed(byte[] bytes) {
        this.secureRandom.setSeed(bytes);
    }

    public SecureRandom getSecureRandom() {
        return secureRandom;
    }

    public void setSecureRandom(SecureRandom random) throws NullPointerException {
        if (random == null) {
            throw new NullPointerException("SecureRandom argument cannot be null.");
        }
        this.secureRandom = random;
    }

    public int getDefaultNextBytesSize() {
        return defaultNextBytesSize;
    }

    public void setDefaultNextBytesSize(int defaultNextBytesSize) throws IllegalArgumentException {
        if (defaultNextBytesSize <= 0) {
            throw new IllegalArgumentException("size value must be a positive integer (1 or larger)");
        }
        this.defaultNextBytesSize = defaultNextBytesSize;
    }

    @Override
    public String nextString() {
        return nextString(getDefaultNextBytesSize());
    }

    @Override
    public String nextString(int numBytes) {
        if (numBytes <= 0) {
            throw new IllegalArgumentException("numBytes argument must be a positive integer (1 or larger)");
        }
        byte[] bytes = new byte[numBytes];
        this.secureRandom.nextBytes(bytes);

        return HexUtil.encodeToString(bytes);
    }
}

