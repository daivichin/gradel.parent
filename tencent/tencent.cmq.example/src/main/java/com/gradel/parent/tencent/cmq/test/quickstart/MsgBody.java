package com.gradel.parent.tencent.cmq.test.quickstart;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MsgBody {

    private String content;
    private int index;

    private int dylayTime;
}
