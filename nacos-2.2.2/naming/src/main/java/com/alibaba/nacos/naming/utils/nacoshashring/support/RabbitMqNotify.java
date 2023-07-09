/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.nacos.naming.utils.nacoshashring.support;

import com.alibaba.nacos.naming.utils.nacoshashring.MessageQueue;
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

    /**
     * 通知gateway
     */
    @Override
    public void notifyGatewayHashRingUpdated() {
        rabbitTemplate.convertAndSend("boot_topic_exchange", "boot.hashring", "0");
    }
}
