package com.alibaba.nacos.naming.utils.nacos_hashring.adapter;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * @program: util
 * @description: bean适配器
 * @author: stop.yc
 * @create: 2023-07-08 11:29
 **/
public class BeanAdapter {
    public static Object[] set2ObjArray(Set<ZSetOperations.TypedTuple<Object>> set) {
        Object[] objArray = new Object[set.size()];
        int index = 0;
        for (ZSetOperations.TypedTuple<Object> tuple : set) {
            objArray[index++] = tuple.getValue();
        }
        return objArray;
    }
}
