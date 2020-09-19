package com.gradel.parent.ali.ahas;

import com.alibaba.csp.sentinel.slots.block.SentinelRpcException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.gradel.parent.common.util.api.common.ResponseManage;
import com.gradel.parent.common.util.api.response.CommonResponse;
import com.gradel.parent.common.util.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date 2019/02/27 上午1:26
 */
@Slf4j
public class SentinelRpcExceptionResolver {

    public static final CommonResponse FLOW_BLOCK_RESP = ResponseManage.flowBlockException(null);

    public static final CommonResponse DEGRADE_BLOCK_RESP = ResponseManage.degradeBlockException(null);

    /**
     * 限流
     *
     * @param ex
     * @return
     */
    public static CommonResponse handle(SentinelRpcException ex) {
        Throwable e = ex.getCause();
        if(e != null){
            if (e instanceof FlowException) {
                return FLOW_BLOCK_RESP;
            } else if (e instanceof DegradeException) {
                return DEGRADE_BLOCK_RESP;
            } else {
                log.error("RPC SentinelRpcException:{}", ExceptionUtil.getAsString(ex));
            }
        }
        //TODO DEFAULT
        return DEGRADE_BLOCK_RESP;
    }
}
