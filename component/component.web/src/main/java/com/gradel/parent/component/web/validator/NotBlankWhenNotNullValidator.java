package com.gradel.parent.component.web.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * ClassName: NotBlankWhenNotNullValidator
 * Function:  NotBlankWhenNotNullValidator
 * @date:      2019/6/19 下午3:56
 * Author     sdeven.chen.dongwei@gmail.com
 * Version    V1.0
 */
public class NotBlankWhenNotNullValidator implements ConstraintValidator<NotBlankWhenNotNull,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }
        else if (value.trim().isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }
}
