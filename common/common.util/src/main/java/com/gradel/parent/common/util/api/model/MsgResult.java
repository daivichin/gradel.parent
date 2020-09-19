package com.gradel.parent.common.util.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/7/4
 * @Description:
 */
@ToString
public abstract class MsgResult {

    @Getter @Setter
    private String code;

    @Getter @Setter
    private String msg;

    @Getter @Setter
    private boolean success;

    public abstract String getName();
}
