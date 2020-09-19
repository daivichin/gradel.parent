package com.gradel.parent.common.util.exception;


import com.gradel.parent.common.util.util.ExceptionUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
*  User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-2-29
 * Depiction:系统异常
 */
public class CommonRuntimeException extends RuntimeException {

    public CommonRuntimeException() {
    }

    public CommonRuntimeException(String message) {
        super(message);
    }

    public CommonRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonRuntimeException(Throwable cause) {
        super(cause);
    }

    public CommonRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String transToString() {
        Throwable throwable = getCause();
        if (throwable != null) {
            StringWriter stringWriter = new StringWriter(50);
            throwable.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.toString();
        }
        return "";
    }


    private static final long serialVersionUID = 5439915454935047936L;

    static {
        ExceptionUtil.class.getName();
    }



    @Override
    public String getMessage() {
        return ExceptionUtil.buildMessage(super.getMessage(), getCause());
    }



    public Throwable getRootCause() {
        Throwable rootCause = null;
        Throwable cause = getCause();
        while (cause != null && cause != rootCause) {
            rootCause = cause;
            cause = cause.getCause();
        }
        return rootCause;
    }

    /**
     * Retrieve the most specific cause of this exception, that is,
     * either the innermost cause (root cause) or this exception itself.
     * <p>Differs from {@link #getRootCause()} in that it falls back
     * to the present exception if there is no root cause.
     * @return the most specific cause (never {@code null})
     * @since 2.0.3
     */
    public Throwable getMostSpecificCause() {
        Throwable rootCause = getRootCause();
        return (rootCause != null ? rootCause : this);
    }

    /**
     * Check whether this exception contains an exception of the given type:
     * either it is of the given class itself or it contains a nested cause
     * of the given type.
     * @param exType the exception type to look for
     * @return whether there is a nested exception of the specified type
     */
    public boolean contains(Class exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        Throwable cause = getCause();
        if (cause == this) {
            return false;
        }
        if (cause instanceof CommonRuntimeException) {
            return ((CommonRuntimeException) cause).contains(exType);
        }
        else {
            while (cause != null) {
                if (exType.isInstance(cause)) {
                    return true;
                }
                if (cause.getCause() == cause) {
                    break;
                }
                cause = cause.getCause();
            }
            return false;
        }
    }
}

