package com.alibaba.nacos.naming.utils.nacos_hashring;

/**
 * @program: util
 * @description: 抽象消息队列类
 * @author: stop.yc
 * @create: 2023-07-06 18:38
 **/
public interface MessageQueue {
    /**
     * 通过gateway哈希环已经更改,需要重新获取哈希环
     */
    void notifyGatewayHashRingUpdated();
}
