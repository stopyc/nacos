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
package com.alibaba.nacos.naming.utils.nacoshashring.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: util
 * @description: 网络地址
 * @author: stop.yc
 * @create: 2023-07-06 15:44
 **/
@Data
@EqualsAndHashCode
public class Address implements Cloneable{

    /**
     * host
     */
    private String ip;

    /**
     * port
     */
    private Integer port;

    /**
     * 是否是虚拟结点
     */
    private Boolean virtualNode;

    /**
     * 包括虚拟结点在内的第几个结点
     */
    private Integer order;

    @Override
    public Address clone() throws CloneNotSupportedException {
        return (Address)super.clone();
    }
}
