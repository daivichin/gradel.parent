package com.gradel.parent.common.util.api.common;

import com.gradel.parent.common.util.api.base.PrimaryKeyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-7
 * @Description:根据主键的请求对象
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommonPrimaryKeyRequest<T> extends CommonRequest implements PrimaryKeyRequest<T>, Serializable {

    private static final long serialVersionUID = 1932645516819856826L;
    @NotNull(message = "ID不能为空")
    private T id;
}
