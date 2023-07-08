package com.alibaba.nacos.naming.utils.nacos_hashring.support.strategy;

import com.alibaba.nacos.naming.utils.nacos_hashring.AbstractHashRing;
import com.alibaba.nacos.naming.utils.nacos_hashring.MessageQueue;
import com.alibaba.nacos.naming.utils.nacos_hashring.strategy.HashRingStrategy;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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

    private static ServiceRegisterStrategy INSTANCE = new ServiceRegisterStrategy();

    private ServiceRegisterStrategy(MessageQueue messageQueue) {
        super(INSTANCE, messageQueue);
    }

    @PostConstruct
    public void init(){
        INSTANCE = this;
        INSTANCE.redisTemplate = this.redisTemplate;
    }
    private ServiceRegisterStrategy() {
        super(INSTANCE, null);
    }

    public static ServiceRegisterStrategy newInstance(@NotNull MessageQueue messageQueue0) {
        hashRingStrategy = INSTANCE;
        messageQueue = messageQueue0;
        return INSTANCE;
    }

    @Override
    public void updateHashRing(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        //获取哈希环
        Set<ZSetOperations.TypedTuple<Object>> hashSet = getOneHashRing(ip, port, 5);
        log.info("开始添加" + serviceName + "ip端口为:" + ip + ":" + port + "的哈希环");
        redisTemplate.opsForZSet().add(PREFIX_HASH_RING, hashSet);
        setNeedNotify(true);
    }
}