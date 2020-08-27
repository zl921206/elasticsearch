package com.kamluen.elasticsearch.kafka;

import com.kamluen2.common.message.KafkaMessageProducer;
import com.kamluen2.common.message.MessageProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KafkaMsgProducer {

    /**
     * 服务地址
     */
    @Value("${apache.kafka.serverAddr}")
    private String serverAddr;

    @Bean
    public MessageProducer producer(){
        return new KafkaMessageProducer(serverAddr);
    }
}
