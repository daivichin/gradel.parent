package com.gradel.parent.tencent.qcloud.cmq;

/**
 * TODO CMQClientException handle all exception caused by client side.
 *
 */
public class CMQClientException extends RuntimeException {

    /**
     * TODO .
     *
     * @param message
     */
    public CMQClientException(String message) {
        super(message);
    }
}
