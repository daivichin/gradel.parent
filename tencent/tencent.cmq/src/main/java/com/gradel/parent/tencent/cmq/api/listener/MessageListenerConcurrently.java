package com.gradel.parent.tencent.cmq.api.listener;

import com.gradel.parent.tencent.cmq.api.Action;
import com.gradel.parent.tencent.qcloud.cmq.Message;
import java.util.List;

public interface MessageListenerConcurrently extends MessageListener {
    Action consumeMessage(final List<Message> msgs);
}
