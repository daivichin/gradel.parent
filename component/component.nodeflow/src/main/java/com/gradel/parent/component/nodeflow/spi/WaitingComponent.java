package com.gradel.parent.component.nodeflow.spi;

import java.util.Map;

/**
 * @author: sdeven.chen.dongwei@gmail.com
 * @date: 2017/10/18 10:54
 * @Description: 延时执行组件
 */
public class WaitingComponent implements NfComponent {

    @Override
    public void execute(Map<String, Object> node, Map<String, Object> ctx) {
        Object waiting = node.get("waiting");
        long timeout = 2000;
        if(waiting!=null) {
            timeout = ((int) waiting) * 1000;
        }
        try {
            Thread.sleep(timeout);
        } catch (Exception e) {
            long startTime = System.currentTimeMillis();
            while((System.currentTimeMillis()-startTime)<timeout);
        }
    }

    @Override
    public String spiId() {
        return "waiting_cmp";
    }
}
