package com.kamluen.elasticsearch.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * rocketMQ 生产者服务类
 * @author zhanglei
 * @date 2018-11-14
 */
@Component
public class RocketMQProvider {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQProvider.class);

    /**
     * 生产者组名
     */
    @Value("${apache.rocketmq.producer.producerGroup}")
    private String producerGroup;

    /**
     * 生产者标题
     */
    @Value("${apache.rocketmq.producer.producerTopic}")
    private String producerTopic;

    /**
     * 生产者标签
     */
    @Value("${apache.rocketmq.producer.topicTags}")
    private String topicTags;

    /**
     * 服务地址
     */
    @Value("${apache.rocketmq.nameServerAddr}")
    private String nameServerAddr;

    private DefaultMQProducer producer;

    @PostConstruct
    public void init() {
        try {
            // 实例化rocketMQ生产者对象，并载入组名
            producer = new DefaultMQProducer(producerGroup);
            // 执行设置nameServer服务地址，多个地址以;隔开
            producer.setNamesrvAddr(nameServerAddr);
            producer.setVipChannelEnabled(false);
            producer.setCreateTopicKey(System.currentTimeMillis() + "");
            /**
             * Producer对象在使用之前必须要调用start初始化，初始化一次即可
             * 注意：切记不可以在每次发送消息时，都调用start方法
             */
            producer.start();
            logger.info("rocketMQ Producer对象调用start初始化成功......");
        } catch (Exception e) {
            logger.error("Producer对象调用start初始化异常，异常信息：{}",e.getMessage());
        }
    }

    public SendResult sendMsg(String msg) {
        SendResult result = null;
        try {
            String key = System.currentTimeMillis() + "";
            //创建一个消息实例，包含 topic、tag 和 消息体
            Message message = new Message(producerTopic, topicTags, key, msg.getBytes());
            StopWatch stop = new StopWatch();
            stop.start();
            result = producer.send(message);
            logger.info("发送响应：MsgId:" + result.getMsgId() + "，发送状态:" + result.getSendStatus());
            stop.stop();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("rocketMQ消息发送异常，异常信息：{}",e.getMessage());
        }
        return result;
    }

    @PreDestroy
    public void destroy() {
        producer.shutdown();
        logger.info("执行producer.shutdown方法成功......");
    }

}
