package com.gradel.parent.common.util.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/3/14
 * @Description:
 */

@Data
@EqualsAndHashCode
public class MySession implements Serializable {
    protected static final long MILLIS_PER_SECOND = 1000L;
    protected static final long MILLIS_PER_MINUTE = 60000L;
    protected static final long MILLIS_PER_HOUR = 3600000L;
    static int bitIndexCounter = 0;
    private static final int ID_BIT_MASK = 0;
    private static final int START_TIMESTAMP_BIT_MASK = 0;
    private static final int STOP_TIMESTAMP_BIT_MASK = 0;
    private static final int LAST_ACCESS_TIME_BIT_MASK = 0;
    private static final int TIMEOUT_BIT_MASK = 0;
    private static final int EXPIRED_BIT_MASK = 0;
    private static final int HOST_BIT_MASK = 0;
    private static final int ATTRIBUTES_BIT_MASK = 0;
    private Serializable id;
    private Date startTimestamp;
    private Date stopTimestamp;
    private Date lastAccessTime;
    private long timeout;
    private boolean expired;
    private String host;
    private Map<Object, Object> attributes;
}
