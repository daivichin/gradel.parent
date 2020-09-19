package com.gradel.parent.component.web.validator;

import javax.validation.groups.Default;

/**
 * ClassName: UValidOpGroup
 * Function:  UValidOpGroup
 * @date:      2019/6/18 下午12:05
 * Author     sdeven.chen.dongwei@gmail.com
 * Version    V1.0
 */
public class UValidOpGroup {
    public interface GET extends Default {}
    public interface PUT extends Default {}
    public interface POST extends Default {}
    public interface DELETE extends Default {}
}
