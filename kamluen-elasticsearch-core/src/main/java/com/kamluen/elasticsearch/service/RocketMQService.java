package com.kamluen.elasticsearch.service;

import org.apache.rocketmq.client.producer.SendResult;

/**
 * RocketMQ 消息服务接口
 */
public interface RocketMQService {

    public SendResult sendMsg(String msg);
}
