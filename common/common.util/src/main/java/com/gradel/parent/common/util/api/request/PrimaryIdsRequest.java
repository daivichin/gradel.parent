package com.gradel.parent.common.util.api.request;

import com.gradel.parent.common.util.api.common.CommonMuliPrimaryKeyRequest;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/10
 * @Description:Long多个主键类型请求对象
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PrimaryIdsRequest extends CommonMuliPrimaryKeyRequest<Long> implements Serializable{

    private static final long serialVersionUID = -542294272675370539L;
}
