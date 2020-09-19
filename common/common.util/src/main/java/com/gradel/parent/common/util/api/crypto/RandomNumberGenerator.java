package com.gradel.parent.common.util.api.crypto;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/6/29
 * @Description:
 */
public interface RandomNumberGenerator {

    String nextString();

    String nextString(int numBytes);
}
