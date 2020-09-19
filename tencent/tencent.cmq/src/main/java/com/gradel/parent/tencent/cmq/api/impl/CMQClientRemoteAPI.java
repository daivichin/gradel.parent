package com.gradel.parent.tencent.cmq.api.impl;

import com.gradel.parent.tencent.cmq.api.PropertyKeyConst;
import com.gradel.parent.tencent.cmq.api.model.SendResult;
import com.gradel.parent.tencent.cmq.api.util.CMQHttpUtil;
import com.gradel.parent.tencent.qcloud.cmq.CMQClientException;
import com.gradel.parent.tencent.qcloud.cmq.CMQServerException;
import com.gradel.parent.tencent.qcloud.cmq.CMQTool;
import com.gradel.parent.tencent.qcloud.cmq.Json.JSONArray;
import com.gradel.parent.tencent.qcloud.cmq.Json.JSONObject;
import com.gradel.parent.tencent.qcloud.cmq.Message;
import com.gradel.parent.common.util.api.model.CommonPageResult;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CMQClientRemoteAPI {

    private CMQConfigAbstract cmqConfig;

    public CMQClientRemoteAPI(CMQConfigAbstract cmqConfig) {
        this.cmqConfig = cmqConfig;
    }

    /**
     * 删除消息
     *
     * @param receiptHandle 消息句柄,获取消息时由服务器返回
     * @throws CMQClientException
     * @throws CMQServerException
     */
    public JSONObject deleteMessage(String queueName, String receiptHandle) throws Exception {
        TreeMap<String, String> param = new TreeMap<String, String>();

        param.put("queueName", queueName);
        param.put("receiptHandle", receiptHandle);

        String result = call("DeleteMessage", param);
        JSONObject jsonObj = new JSONObject(result);
        return jsonObj;
    }


    /**
     * 获取消息
     * <p>
     * //     * @param pollingWaitSeconds 请求最长的Polling等待时间
     *
     * @return 服务器返回消息
     * @throws CMQClientException
     * @throws CMQServerException
     */
    public Message receiveMessage(String queueName) throws Exception {
        TreeMap<String, String> param = new TreeMap<>();
        //pollingWaitSeconds 请求最长的Polling等待时间
        int pollingWaitSeconds = cmqConfig.getPollingWaitSeconds();
        param.put("queueName", queueName);
        if (pollingWaitSeconds > 0) {
            //用户拉取消息等待时间
            param.put(PropertyKeyConst.SOCKET_READ_USERPOLLINGWAIT_MILLISECOND, Integer.toString((pollingWaitSeconds + 3) * 1000));
            //服务器拉取超时时间
            param.put(PropertyKeyConst.CMQ_PARAM_POLLINGWAITSECONDS, Integer.toString(pollingWaitSeconds));
        } else {
            param.put(PropertyKeyConst.SOCKET_READ_USERPOLLINGWAIT_MILLISECOND, Integer.toString(CMQConfigAbstract.DEFAULT_USER_POLLING_WAIT_MILLISECOND));
        }

        String result = call("ReceiveMessage", param);
        JSONObject jsonObj = new JSONObject(result);
        int code = jsonObj.getInt("code");
        if (code != 0)
            throw new CMQServerException(code, jsonObj.optString("message"), jsonObj.optString("requestId"));

        Message msg = new Message();
        msg.msgId = jsonObj.getString("msgId");
        msg.receiptHandle = jsonObj.getString("receiptHandle");
        msg.msgBody = jsonObj.getString("msgBody");
        msg.enqueueTime = jsonObj.getLong("enqueueTime");
        msg.nextVisibleTime = jsonObj.getLong("nextVisibleTime");
        msg.firstDequeueTime = jsonObj.getLong("firstDequeueTime");
        msg.dequeueCount = jsonObj.getInt("dequeueCount");

        return msg;
    }

    /**
     * 批量获取消息
     *
     * @param queueName queueName
     * @return 服务器返回消息列表
     * @throws CMQClientException
     * @throws CMQServerException
     */
    public List<Message> batchReceiveMessage(String queueName) throws Exception {
        TreeMap<String, String> param = new TreeMap<>();

        param.put("queueName", queueName);
//        numOfMsg  准备获取消息数
        int numOfMsg = cmqConfig.getBatchReceiveMsgSize();
        param.put("numOfMsg", Integer.toString(numOfMsg));

        //        pollingWaitSeconds 请求最长的Polling等待时间
        int pollingWaitSeconds = cmqConfig.getPollingWaitSeconds();
        if (pollingWaitSeconds > 0) {
            //用户拉取消息等待时间
            param.put(PropertyKeyConst.SOCKET_READ_USERPOLLINGWAIT_MILLISECOND, Integer.toString((pollingWaitSeconds + 3) * 1000));
            //服务器拉取超时时间
            param.put(PropertyKeyConst.CMQ_PARAM_POLLINGWAITSECONDS, Integer.toString(pollingWaitSeconds));
        } else {
            param.put(PropertyKeyConst.SOCKET_READ_USERPOLLINGWAIT_MILLISECOND, Integer.toString(CMQConfigAbstract.DEFAULT_USER_POLLING_WAIT_MILLISECOND));
        }

        String result = call("BatchReceiveMessage", param);
        JSONObject jsonObj = new JSONObject(result);
        int code = jsonObj.getInt("code");
        if (code != 0)
            throw new CMQServerException(code, jsonObj.optString("message"), jsonObj.optString("requestId"));

        ArrayList<Message> vtMessage = new ArrayList<Message>();

        JSONArray jsonArray = jsonObj.getJSONArray("msgInfoList");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
            Message msg = new Message();
            msg.msgId = obj.getString("msgId");
            msg.receiptHandle = obj.getString("receiptHandle");
            msg.msgBody = obj.getString("msgBody");
            msg.enqueueTime = obj.getLong("enqueueTime");
            msg.nextVisibleTime = obj.getLong("nextVisibleTime");
            msg.firstDequeueTime = obj.getLong("firstDequeueTime");
            msg.dequeueCount = obj.getInt("dequeueCount");

            vtMessage.add(msg);
        }

        return vtMessage;
    }

    /**
     * 批量删除消息
     *
     * @param vtReceiptHandle 消息句柄列表，获取消息时由服务器返回
     * @throws CMQClientException
     * @throws CMQServerException
     */
    public SendResult batchDeleteMessage(String queueName, List<String> vtReceiptHandle) throws Exception {
        SendResult sendResult = new SendResult();
        sendResult.setQueue(queueName);
        if (vtReceiptHandle.isEmpty()) {
            sendResult.setSuccess(true);
            sendResult.setErrCode(0);
            sendResult.setErrMsg("成功");
            return sendResult;
        }

        TreeMap<String, String> param = new TreeMap<>();
        param.put("queueName", queueName);
        for (int i = 0; i < vtReceiptHandle.size(); i++) {
            String k = "receiptHandle." + Integer.toString(i + 1);
            param.put(k, vtReceiptHandle.get(i));
        }

        String result = call("BatchDeleteMessage", param);
        JSONObject jsonObj = new JSONObject(result);

        int code = jsonObj.getInt("code");
        if (code != 0) {
            sendResult.setErrMsg(jsonObj.optString("message"));
        } else {
            sendResult.setSuccess(true);
        }
        sendResult.setRequestId(jsonObj.optString("requestId"));
        sendResult.setErrCode(code);
        return sendResult;
    }

    private String call(String action, TreeMap<String, String> param) throws Exception {
        String rsp = "";
        try {
            param.put("Action", action);
            initBaseParam(param);
            StringBuilder src = new StringBuilder();
            src.append(cmqConfig.getMethod()).append(cmqConfig.getHost()).append(cmqConfig.getPath()).append("?");

            boolean flag = false;
            for (String key : param.keySet()) {
                if (flag)
                    src.append("&");
                src.append(key.replace("_", ".")).append("=").append(param.get(key));
                flag = true;
            }
            param.put("Signature", CMQTool.sign(src.toString(), cmqConfig.getSecretKey(), cmqConfig.getSignMethod()));
            StringBuilder url = new StringBuilder();
            StringBuilder req = new StringBuilder();
            if (cmqConfig.getMethod().equals("GET")) {
                url.append(cmqConfig.getEndpoint()).append(cmqConfig.getPath()).append("?");
                flag = false;
                for (String key : param.keySet()) {
                    if (flag)
                        url.append("&");
                    url.append(key).append("=").append(URLEncoder.encode(param.get(key), "utf-8"));
                    flag = true;
                }
                if (url.length() > 2048)
                    throw new CMQClientException("URL length is larger than 2K when use GET method");
            } else {
                url.append(cmqConfig.getEndpoint()).append(cmqConfig.getPath());
                flag = false;
                for (String key : param.keySet()) {
                    if (flag)
                        req.append("&");
                    req.append(key).append("=").append(URLEncoder.encode(param.get(key), "utf-8"));
                    flag = true;
                }
            }

            int connectTimeoutMilliseconds = getConnectTimeoutMillisecondsFromParam(param, PropertyKeyConst.MQ_HTTP_CONNECT_WAIT_MILLISECOND);
            int readTimeoutMilliseconds = getConnectTimeoutMillisecondsFromParam(param, PropertyKeyConst.MQ_HTTP_READ_WAIT_MILLISECOND);
            rsp = CMQHttpUtil.request(cmqConfig.getMethod(), url.toString(), req.toString(), cmqConfig.isKeepalive(), connectTimeoutMilliseconds, readTimeoutMilliseconds);
        } catch (Exception e) {
            throw e;
        }
        return rsp;
    }

    private int getConnectTimeoutMillisecondsFromParam(TreeMap<String, String> param, String key) {
        int timeoutMilliseconds = CMQConfigAbstract.DEFAULT_USER_POLLING_WAIT_MILLISECOND;
        if (param.containsKey(key)) {
            timeoutMilliseconds = Integer.parseInt(param.get(key));

        }else if (param.containsKey(PropertyKeyConst.SOCKET_READ_USERPOLLINGWAIT_MILLISECOND)) {
            timeoutMilliseconds = Integer.parseInt(param.get(PropertyKeyConst.SOCKET_READ_USERPOLLINGWAIT_MILLISECOND));
        }
        return timeoutMilliseconds;
    }

    private void initBaseParam(TreeMap<String, String> param) {
        param.put("Nonce", Integer.toString(ThreadLocalRandom.current().nextInt(java.lang.Integer.MAX_VALUE)));
        param.put("SecretId", cmqConfig.getSecretId());
        param.put("Timestamp", Long.toString(System.currentTimeMillis() / 1000));
        param.put("RequestClient", cmqConfig.getVersion());
        param.put("SignatureMethod", cmqConfig.getSignatureMethod());
    }

    /**
     * list queue
     *
     * @param searchWord String
     * @param offset     从0开始,分页时本页获取队列列表的起始位置。如果填写了该值，必须也要填写 limit 。该值缺省时，后台取默认值 0
     * @param limit      分页时本页获取队列的个数，如果不传递该参数，则该参数默认为 20，最大值为 50。
     * @return totalCount int
     * @throws Exception
     * @throws CMQClientException https://cloud.tencent.com/document/product/406/5833
     */
    public SendResult<CommonPageResult<String>> listQueue(String searchWord, int offset, int limit) throws Exception {
        TreeMap<String, String> param = new TreeMap<String, String>();
        if (!searchWord.equals("")) {
            param.put("searchWord", searchWord);
        }
        if (offset < 0) {
            offset = 0;
        }

        if (limit <= 0 || limit > 50) {
            limit = 20;
        }


        param.put("offset", Integer.toString(offset));
        param.put("limit", Integer.toString(limit));

        String result = call("ListQueue", param);
        JSONObject jsonObj = new JSONObject(result);
        int code = jsonObj.getInt("code");
        SendResult<CommonPageResult<String>> sendResult = new SendResult();
        if (code != 0) {
            sendResult.setErrMsg(jsonObj.optString("message"));
            sendResult.setSuccess(false);
            return sendResult;
        } else {
            sendResult.setSuccess(true);
        }
        sendResult.setRequestId(jsonObj.optString("requestId"));
        sendResult.setErrCode(code);


        int totalCount = jsonObj.getInt("totalCount");
        int totalPage = totalCount / limit + ((totalCount % limit == 0) ? 0 : 1);

        CommonPageResult<String> pageResult = new CommonPageResult<>();
        pageResult.setTotalCount(totalCount);
        pageResult.setTotalPage(totalPage);

        JSONArray jsonArray = jsonObj.getJSONArray("queueList");

        List<String> queueList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
            queueList.add(obj.getString("queueName"));
        }
        pageResult.setData(queueList);
        sendResult.setData(pageResult);

        return sendResult;
    }

    /**
     * 加载所有主题，默认最大只能加载limit * maxOffset条数据 50000
     * @return
     * @throws Exception
     */
    public SendResult<List<String>> listAllQueue() throws Exception {
        int offset = 0;
        int limit = 50;
        //TODO 默认最大只能加载limit * maxOffset条数据
        int maxOffset = 1000;
        boolean hasNext = false;
        SendResult<List<String>> returnDataResult = new SendResult<>();
        SendResult<CommonPageResult<String>> resultSendResult = null;
        do {
            resultSendResult = listQueue("", offset, limit);
            if (resultSendResult.isSuccess()) {
                offset += limit;
                CommonPageResult<String> pageResult = resultSendResult.getData();
                if (returnDataResult.getData() == null) {
                    returnDataResult.setData(pageResult.getData());
                } else {
                    returnDataResult.getData().addAll(pageResult.getData());
                }
                hasNext = pageResult.getTotalCount() > offset;
                returnDataResult.setSuccess(true);
            } else {
//                hasNext = false;
                returnDataResult.setSuccess(false);
                break;
            }

        } while (hasNext && offset < maxOffset);
        if (resultSendResult != null) {
            returnDataResult.setErrCode(resultSendResult.getErrCode());
            returnDataResult.setErrMsg(resultSendResult.getErrMsg());
            returnDataResult.setRequestId(resultSendResult.getRequestId());
            returnDataResult.setQueue(resultSendResult.getQueue());
        }
        return returnDataResult;
    }

    public SendResult<String> sendMessageByParam(String queueName, String msgBody, int delayTime) throws Exception {
        return sendMessageByParam(queueName, msgBody, delayTime, 0, 0);
    }

    public SendResult<String> sendMessageByParam(String queueName, String msgBody, int delayTime, int connectTimeoutMilliseconds, int readTimeoutMilliseconds) throws Exception {
        TreeMap<String, String> param = new TreeMap<String, String>();

        param.put("queueName", queueName);
        param.put("msgBody", msgBody);
        param.put("delaySeconds", Integer.toString(delayTime));
        if(connectTimeoutMilliseconds > 0){
            param.put(PropertyKeyConst.MQ_HTTP_CONNECT_WAIT_MILLISECOND, Integer.toString(connectTimeoutMilliseconds));
        }
        if(readTimeoutMilliseconds > 0){
            param.put(PropertyKeyConst.MQ_HTTP_READ_WAIT_MILLISECOND, Integer.toString(readTimeoutMilliseconds));
        }

        String result = call("SendMessage", param);
        JSONObject jsonObj = new JSONObject(result);
        SendResult sendResult = new SendResult();
        sendResult.setQueue(queueName);
        int code = jsonObj.getInt("code");
        if (code != 0) {
            sendResult.setErrMsg(jsonObj.optString("message"));
        } else {
            sendResult.setSuccess(true);
        }
        sendResult.setRequestId(jsonObj.optString("requestId"));
        sendResult.setErrCode(code);
        sendResult.setData(jsonObj.optString("msgId"));
        return sendResult;

    }
}
