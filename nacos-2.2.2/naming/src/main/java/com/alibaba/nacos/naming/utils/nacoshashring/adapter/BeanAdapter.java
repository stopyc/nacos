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
package com.alibaba.nacos.naming.utils.nacoshashring.adapter;

import org.springframework.data.redis.core.ZSetOperations;
import java.util.Set;

/**
 * @program: util
 * @description: bean适配器
 * @author: stop.yc
 * @create: 2023-07-08 11:29
 **/
public class BeanAdapter {
    /**
     * set集合转换为obj数组
     * @param set :Set<ZSetOperations.TypedTuple<Object>> set
     * @return: objArray
     */
    public static Object[] set2ObjArray(Set<ZSetOperations.TypedTuple<Object>> set) {
        Object[] objArray = new Object[set.size()];
        int index = 0;
        for (ZSetOperations.TypedTuple<Object> tuple : set) {
            objArray[index++] = tuple.getValue();
        }
        return objArray;
    }
}
