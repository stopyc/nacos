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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @program: util
 * @description: 服务注册
 * @author: stop.yc
 * @create: 2023-07-06 18:08
 **/
@Slf4j
@Component
public class ServiceRegisterStrategy extends AbstractHashRing implements HashRingStrategy {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 单例
     */
    private static ServiceRegisterStrategy INSTANCE = new ServiceRegisterStrategy();

    private ServiceRegisterStrategy(MessageQueue messageQueue) {
        super(INSTANCE, messageQueue);
    }

    /**
     * 配合spring注入
     */
    @PostConstruct
    public void init(){
        INSTANCE = this;
        INSTANCE.redisTemplate = this.redisTemplate;
    }
    private ServiceRegisterStrategy() {
        super(INSTANCE, null);
    }

    /**
     * 外部访问接口
     * @param messageQueue0: 消息队列实现
     * @return: ServiceDeregisterStrategy
     */
    public static ServiceRegisterStrategy newInstance(@NonNull MessageQueue messageQueue0) {
        hashRingStrategy = INSTANCE;
        messageQueue = messageQueue0;
        return INSTANCE;
    }

    /**
     * 主要实现
     * @param namespaceId: namespaceId
     * @param serviceName: service name
     * @param ip: ip address
     * @param port: port number
     * @param healthy: boolean
     * @param enabled: boolean
     */
    @Override
    public void updateHashRing(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        //获取哈希环
        Set<ZSetOperations.TypedTuple<Object>> hashSet = getOneHashRing(ip, port, 5);
        log.info("开始添加" + serviceName + "ip端口为:" + ip + ":" + port + "的哈希环");
        redisTemplate.opsForZSet().add(PREFIX_HASH_RING, hashSet);
        setNeedNotify(true);
    }
}