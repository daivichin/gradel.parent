package com.gradel.parent.component.web.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

/**
 * ClassName: AtLeastOneNotNullValidator
 * Function:  AtLeastOneNotNullValidator
 * @date:      2019/6/19 下午3:56
 * Author     sdeven.chen.dongwei@gmail.com
 * Version    V1.0
 */
public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull,Object> {
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        boolean valid = true;
        for (Field f : object.getClass().getDeclaredFields()) {
            AtLeastOneNotNullTarget ann = f.getDeclaredAnnotation(AtLeastOneNotNullTarget.class);
            if (null != ann) {
                valid = false;
                f.setAccessible(true);

                try {
                    Object value = f.get(object);
                    if (null != value) {
                        if (ann.blankable() || !(value instanceof String)) {
                            return true;
                        }
                        else if (!((String)value).trim().isEmpty()){
                            return true;
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return valid;
    }
}
