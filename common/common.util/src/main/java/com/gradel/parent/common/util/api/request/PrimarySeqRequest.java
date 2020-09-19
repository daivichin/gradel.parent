package com.gradel.parent.common.util.api.request;

import com.gradel.parent.common.util.api.common.CommonPrimaryKeyRequest;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/10
 * @Description:String类型主键请求对象
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PrimarySeqRequest extends CommonPrimaryKeyRequest<String> implements Serializable{
    private static final long serialVersionUID = -460586011538779370L;
}
