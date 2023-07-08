package com.alibaba.nacos.naming.utils.nacos_hashring.strategy;


/**
 * 哈希环方法更新缓存策略接口
 * @author YC104
 */
public interface HashRingStrategy {
    /**
     * Nacos接收服务变更消息,更新哈希环到缓存中
     * @param namespaceId: namespaceId
     * @param serviceName: service name
     * @param ip: ip address
     * @param port: port number
     * @param healthy: boolean
     * @param enabled: boolean
     */
    void updateHashRing(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled);
}
