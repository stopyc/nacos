package com.alibaba.nacos.naming.utils.nacos_hashring.template;

import com.alibaba.nacos.naming.utils.nacos_hashring.MessageQueue;
import com.alibaba.nacos.naming.utils.nacos_hashring.strategy.HashRingStrategy;
import org.springframework.stereotype.Component;

/**
 * @program: util
 * @description: 哈希环抽象模板方法
 * @author: stop.yc
 * @create: 2023-07-06 18:27
 **/
@Component
public abstract class AbstractHashRingTemplate {

    private static final String SERVICE_NAME = "ws-service";

    protected static HashRingStrategy hashRingStrategy;

    protected static MessageQueue messageQueue;

    protected boolean needNotify;


    public AbstractHashRingTemplate (HashRingStrategy hashRingStrategy0, MessageQueue messageQueue0) {
        hashRingStrategy = hashRingStrategy0;
        messageQueue = messageQueue0;
    }

    public final void updateHashRing2Cache(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        boolean correspondingServiceFlag = this.isCorresponding(serviceName);
        if (!correspondingServiceFlag) {
            return;
        }
        this.updateHashRing2CacheInternal(namespaceId, serviceName, ip, port, healthy, enabled);
        if (needNotify) {
            this.notifyGateway();
        }
    }

    private boolean isCorresponding(String serviceName) {
        return SERVICE_NAME.equals(serviceName);
    }

    private void notifyGateway() {
        messageQueue.notifyGatewayHashRingUpdated();
    }

    private void updateHashRing2CacheInternal(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        hashRingStrategy.updateHashRing(namespaceId, serviceName, ip, port, healthy, enabled);
    }

    public void setNeedNotify(boolean needNotify) {
        this.needNotify = needNotify;
    }
}
