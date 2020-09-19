package com.gradel.parent.common.util.api.common;

import com.gradel.parent.common.util.api.base.MuliPrimaryKeyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-7
 * 公共多个ID请求对象
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommonMuliPrimaryKeyRequest<T> extends CommonRequest implements MuliPrimaryKeyRequest<T>, Serializable {

    private static final long serialVersionUID = 7953105286146212685L;
    /**
     * ID集合
     */
    @NotNull(message = "ID集合不能为空")
    private List<T> ids;
}
