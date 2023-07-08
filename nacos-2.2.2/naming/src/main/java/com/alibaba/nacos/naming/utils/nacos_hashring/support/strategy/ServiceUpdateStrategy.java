package com.alibaba.nacos.naming.utils.nacos_hashring.support.strategy;

import com.alibaba.nacos.naming.utils.nacos_hashring.AbstractHashRing;
import com.alibaba.nacos.naming.utils.nacos_hashring.MessageQueue;
import com.alibaba.nacos.naming.utils.nacos_hashring.strategy.HashRingStrategy;
import com.alibaba.nacos.naming.utils.nacos_hashring.template.AbstractHashRingTemplate;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    private static ServiceUpdateStrategy INSTANCE = new ServiceUpdateStrategy();

    private ServiceUpdateStrategy(MessageQueue messageQueue) {
        super(INSTANCE, messageQueue);
    }

    @PostConstruct
    public void init(){
        INSTANCE = this;
        INSTANCE.redisTemplate = this.redisTemplate;
    }
    private ServiceUpdateStrategy() {
        super(INSTANCE, null);
    }

    public static ServiceUpdateStrategy newInstance(@NotNull MessageQueue messageQueue0) {
        hashRingStrategy = INSTANCE;
        messageQueue = messageQueue0;
        return INSTANCE;
    }

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