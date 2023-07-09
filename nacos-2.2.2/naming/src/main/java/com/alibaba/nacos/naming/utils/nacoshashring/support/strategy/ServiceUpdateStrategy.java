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
package com.alibaba.nacos.naming.utils.nacoshashring.support.strategy;

import com.alibaba.nacos.naming.utils.nacoshashring.AbstractHashRing;
import com.alibaba.nacos.naming.utils.nacoshashring.MessageQueue;
import com.alibaba.nacos.naming.utils.nacoshashring.strategy.HashRingStrategy;
import com.alibaba.nacos.naming.utils.nacoshashring.template.AbstractHashRingTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @program: util
 * @description: 服务注册
 * @author: stop.yc
 * @create: 2023-07-06 18:08
 **/
@Slf4j
@Component
public class ServiceUpdateStrategy extends AbstractHashRing implements HashRingStrategy {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 单例
     */
    private static ServiceUpdateStrategy INSTANCE = new ServiceUpdateStrategy();

    private ServiceUpdateStrategy(MessageQueue messageQueue) {
        super(INSTANCE, messageQueue);
    }

    /**
     * 配合spring
     */
    @PostConstruct
    public void init(){
        INSTANCE = this;
        INSTANCE.redisTemplate = this.redisTemplate;
    }
    private ServiceUpdateStrategy() {
        super(INSTANCE, null);
    }

    /**
     * 外部访问
     * @param messageQueue0: 消息队列实现
     * @return: ServiceDeregisterStrategy
     */
    public static ServiceUpdateStrategy newInstance(@NonNull MessageQueue messageQueue0) {
        hashRingStrategy = INSTANCE;
        messageQueue = messageQueue0;
        return INSTANCE;
    }

    /**
     * 更新哈希环
     * @param namespaceId: namespaceId
     * @param serviceName: service name
     * @param ip: ip address
     * @param port: port number
     * @param healthy: boolean
     * @param enabled: boolean
     */
    @Override
    public void updateHashRing(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        AbstractHashRingTemplate abstractHashRingTemplate;
        if (healthy  && enabled) {
            abstractHashRingTemplate = ServiceRegisterStrategy.newInstance(messageQueue);
        } else {
            abstractHashRingTemplate = ServiceDeregisterStrategy.newInstance(messageQueue);
        }
        abstractHashRingTemplate.updateHashRing2Cache(namespaceId, serviceName, ip, port, healthy, enabled);
        setNeedNotify(false);
    }
}