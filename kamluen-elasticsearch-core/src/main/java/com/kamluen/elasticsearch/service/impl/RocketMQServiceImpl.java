package com.kamluen.elasticsearch.service.impl;

import com.kamluen.elasticsearch.rocketmq.RocketMQProvider;
import com.kamluen.elasticsearch.service.RocketMQService;
import org.apache.rocketmq.client.producer.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RocketMQServiceImpl implements RocketMQService {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQServiceImpl.class);

    @Resource
    private RocketMQProvider rocketMQProvider;

    @Override
    public SendResult sendMsg(String msg) {
        SendResult result = null;
        try {
            result = rocketMQProvider.sendMsg(msg);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("记录关键词推送rocketMQ异常，异常信息{}", e.getMessage());
        }
        return result;

    }
}
