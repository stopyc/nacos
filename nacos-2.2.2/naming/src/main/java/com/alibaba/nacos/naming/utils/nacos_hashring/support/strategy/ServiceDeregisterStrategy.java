package com.alibaba.nacos.naming.utils.nacos_hashring.support.strategy;

import com.alibaba.nacos.naming.utils.nacos_hashring.AbstractHashRing;
import com.alibaba.nacos.naming.utils.nacos_hashring.MessageQueue;
import com.alibaba.nacos.naming.utils.nacos_hashring.adapter.BeanAdapter;
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
public class ServiceDeregisterStrategy extends AbstractHashRing implements HashRingStrategy {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private static ServiceDeregisterStrategy INSTANCE = new ServiceDeregisterStrategy();

    private ServiceDeregisterStrategy(MessageQueue messageQueue) {
        super(INSTANCE, messageQueue);
    }

    @PostConstruct
    public void init(){
        INSTANCE = this;
        INSTANCE.redisTemplate = this.redisTemplate;
    }
    private ServiceDeregisterStrategy() {
        super(INSTANCE, null);
    }

    public static ServiceDeregisterStrategy newInstance(@NotNull MessageQueue messageQueue0) {
        hashRingStrategy = INSTANCE;
        messageQueue = messageQueue0;
        return INSTANCE;
    }

    @Override
    public void updateHashRing(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        //获取哈希环
        Set<ZSetOperations.TypedTuple<Object>> hashSet = getOneHashRing(ip, port, DEFAULT_VIRTUAL_NODE_NUM);
        log.info("开始下线服务,服务名称为" + serviceName);
        Object[] objArray = BeanAdapter.set2ObjArray(hashSet);
        redisTemplate.opsForZSet().remove(PREFIX_HASH_RING, objArray);
        setNeedNotify(true);
    }
}