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
package com.alibaba.nacos.naming.utils.nacoshashring.template;

import com.alibaba.nacos.naming.utils.nacoshashring.MessageQueue;
import com.alibaba.nacos.naming.utils.nacoshashring.strategy.HashRingStrategy;
import org.springframework.stereotype.Component;

/**
 * @program: util
 * @description: 哈希环抽象模板方法
 * @author: stop.yc
 * @create: 2023-07-06 18:27
 **/
@Component
public abstract class AbstractHashRingTemplate {

    /**
     * 服务名称
     */
    private static final String SERVICE_NAME = "ws-service";

    /**
     * 哈希环更新策略
     */
    protected static HashRingStrategy hashRingStrategy;

    /**
     * 通知的消息队列
     */
    protected static MessageQueue messageQueue;

    /**
     * 是否需要通知
     */
    protected boolean needNotify;

    public AbstractHashRingTemplate(HashRingStrategy hashRingStrategy0, MessageQueue messageQueue0) {
        hashRingStrategy = hashRingStrategy0;
        messageQueue = messageQueue0;
    }

    /**
     * 模板方法:更新哈希环到缓存中
     *
     * @param namespaceId: namespaceId
     * @param serviceName: serviceName
     * @param ip:          ip
     * @param port:        port
     * @param healthy:     是否健康
     * @param enabled:     是否可用
     */
    public final void updateHashRing2Cache(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        boolean correspondingServiceFlag = this.isCorresponding(serviceName);
        if (!correspondingServiceFlag) {
            return;
        }
        this.updateHashRing2CacheInternal(namespaceId, serviceName, ip, port, healthy, enabled);
        this.notifyGateway();
    }

    /**
     * 是否是需要维护哈希环的服务
     *
     * @param serviceName: 服务名称
     * @return: true or false
     */
    private boolean isCorresponding(String serviceName) {
        return serviceName.contains(SERVICE_NAME);
    }

    /**
     * 通知gateway
     */
    private void notifyGateway() {
        if (needNotify) {
            messageQueue.notifyGatewayHashRingUpdated();
        }
    }

    /**
     * 模板方法中的可变算法,这里使用策略模式
     * @param namespaceId: namespaceId
     * @param serviceName: serviceName
     * @param ip:          ip
     * @param port:        port
     * @param healthy:     是否健康
     * @param enabled:     是否可用
     */
    private void updateHashRing2CacheInternal(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        hashRingStrategy.updateHashRing(namespaceId, serviceName, ip, port, healthy, enabled);
    }

    /**
     * 设置是否需要通知gateway,可能已经被通知过了
     * @param needNotify: 是否需要通知
     */
    public void setNeedNotify(boolean needNotify) {
        this.needNotify = needNotify;
    }
}
