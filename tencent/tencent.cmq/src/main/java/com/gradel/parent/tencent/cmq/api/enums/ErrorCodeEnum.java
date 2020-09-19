package com.gradel.parent.tencent.cmq.api.enums;

import lombok.Getter;
import lombok.Setter;

/*
6010	10150	delete message partially failed	批量删除消息，部分失败。失败部分的每一条都有错误 message 提示。失败很可能是因为 receipt handle 失效。
4430	10260	receipt handle is invalid	消息句柄无效。句柄失效原因，详见 删除消息。
6020	10290	batch delete message failed	批量删除消息失败。
4000	10470	receiptHandle error	receiptHandle 错误。receiptHandle 是字符串化
*/
public enum ErrorCodeEnum {

    SUCCESS(0, "成功"),
    FAILURE_HTTP_STATUS(-200, "HTTP STATUS NOT EQ 200"),
    FAILURE_(-1, "失败"),
    FAILURE_MSG_LEN_MAX(-2, "客户端发送异常(消息长度过长)"),
    FAILURE_MSG_CONTEN_EMPTY(-3, "消息内容为空"),
    FAILURE_MSG_QUEUE_EMPTY(-4, "消息队列名称为空"),
    FAILURE_MSG_SERIALIZER_EMPTY(-5, "序列化对象为空"),
    FAILURE_MSG_SERIALIZER_ERR(-6, "反序列化对象出错"),

    ;

    private ErrorCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Getter
    @Setter
    private int code;
    @Getter @Setter
    private String desc;
}
