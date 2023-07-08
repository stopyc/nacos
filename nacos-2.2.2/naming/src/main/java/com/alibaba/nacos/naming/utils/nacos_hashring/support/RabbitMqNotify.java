package com.alibaba.nacos.naming.utils.nacos_hashring.support;

import com.alibaba.nacos.naming.utils.nacos_hashring.MessageQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: util
 * @description: 集成rabbitmq进行通知gateway
 * @author: stop.yc
 * @create: 2023-07-06 20:23
 **/
@Component
public class RabbitMqNotify implements MessageQueue {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void notifyGatewayHashRingUpdated() {
        rabbitTemplate.convertAndSend("boot_topic_exchange", "boot.hashring", "0");
    }
}
