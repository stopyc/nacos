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
package com.alibaba.nacos.naming.utils.nacoshashring;

import com.alibaba.nacos.naming.utils.nacoshashring.entity.Address;
import com.alibaba.nacos.naming.utils.nacoshashring.strategy.HashRingStrategy;
import com.alibaba.nacos.naming.utils.nacoshashring.template.AbstractHashRingTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @program: util
 * @description: 抽象哈希环
 * @author: stop.yc
 * @create: 2023-07-06 20:32
 **/
@Slf4j
@Component
public abstract class AbstractHashRing extends AbstractHashRingTemplate {

    /**
     * 默认虚拟结点数量
     */
    protected static final Integer DEFAULT_VIRTUAL_NODE_NUM = 5;

    /**
     * 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
     */
    protected static SortedMap<Double, Address> virtualNodes = new TreeMap<>();

    /**
     * 缓存前缀
     */
    protected static final String PREFIX_HASH_RING = "hashring:";

    /**
     * 读写互斥锁
     */
    protected static final Object MUTEX = new Object();


    public AbstractHashRing(HashRingStrategy hashRingStrategy, MessageQueue messageQueue) {
        super(hashRingStrategy, messageQueue);
    }

    /**
     * 获取虚拟哈希结点
     * @param realNode: 真实结点
     * @param virtualNum: 虚拟结点数量
     * @param set: 存放的容器
     * @return: 容器返回
     */
    protected Set<ZSetOperations.TypedTuple<Object>> getVirtualNodes(Address realNode, int virtualNum, Set<ZSetOperations.TypedTuple<Object>> set) {
        try {
            Address virtualAddress;
            for (int i = 0; i < virtualNum; i++) {
                virtualAddress = realNode.clone();
                virtualAddress.setVirtualNode(true);
                virtualAddress.setOrder(i + 1);
                String virtualNodeName = realNode.getIp() + ":" + realNode.getPort() + "&&VN" + i + 1;
                int hash = getHash(virtualNodeName);
                set.add(new DefaultTypedTuple<>(virtualAddress, (double) hash));
            }
        } catch (CloneNotSupportedException ignored) {
        }
        return set;
    }

    /**
     * 获取虚拟哈希结点
     * @param realNode: 真实结点
     * @param set: 存放的容器
     * @return: 容器返回
     */
    protected Set<ZSetOperations.TypedTuple<Object>> getVirtualNodes(Address realNode, Set<ZSetOperations.TypedTuple<Object>> set) {
        return getVirtualNodes(realNode, DEFAULT_VIRTUAL_NODE_NUM, set);
    }


    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     * @param str :key Str
     * @return :hashCode
     */
    protected static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    /**
     * 获取一个哈希环
     * @param ip: ip
     * @param port: 端口
     * @param virtualNodesCount: 虚拟结点的数量
     * @return: 返回哈希set
     */
    protected Set<ZSetOperations.TypedTuple<Object>> getOneHashRing(String ip, int port, int virtualNodesCount) {

        Set<ZSetOperations.TypedTuple<Object>> set = new TreeSet<>();

        //2. obtainTheNameOfTheRealNode
        String realNodeName = ip + ":" + port;

        //3. obtainHashBasedOnName
        int realNodeHash = getHash(realNodeName);

        log.info("ws服务真实节点 ( {}:{} )的哈希值为 {}", ip, port, realNodeHash);

        Address address = new Address();
        address.setVirtualNode(false);
        address.setIp(ip);
        address.setPort(port);
        address.setOrder(0);
        set.add(new DefaultTypedTuple<>(address, (double) realNodeHash));

        getVirtualNodes(address, set);

        return set;
    }
}
