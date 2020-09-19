package com.gradel.parent.common.util.api.request;

import com.gradel.parent.common.util.api.common.CommonPrimaryKeyRequest;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/10
 * @Description:Long类型请求对象
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PrimaryIdRequest extends CommonPrimaryKeyRequest<Long> implements Serializable{

    private static final long serialVersionUID = -7978906852152285946L;
}
