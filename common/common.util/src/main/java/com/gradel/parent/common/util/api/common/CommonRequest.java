package com.gradel.parent.common.util.api.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-7
 * Depiction: 请求参数基类
 */
@Data
@ToString
@EqualsAndHashCode
public class CommonRequest implements Serializable{

    private static final long serialVersionUID = -5747402125297698498L;
    /**
     * 当前登录人Id
     */
    private String currentUserId;

    /**
     * 发起流水号
     */
    private String initiationID;

    public CommonRequest(){

    }
}
