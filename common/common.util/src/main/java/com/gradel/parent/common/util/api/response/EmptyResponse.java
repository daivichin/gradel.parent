package com.gradel.parent.common.util.api.response;

import java.io.Serializable;

public class EmptyResponse implements Serializable {

    public final static EmptyResponse emptyResponse = new EmptyResponse();

    public static EmptyResponse empty(){
        return emptyResponse;
    }
}
