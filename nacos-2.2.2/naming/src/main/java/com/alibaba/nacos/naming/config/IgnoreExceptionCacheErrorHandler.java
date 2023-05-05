///*
// * Copyright 1999-2018 Alibaba Group Holding Ltd.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.alibaba.nacos.naming.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.Cache;
//import org.springframework.cache.interceptor.CacheErrorHandler;
//
///**
// * 当缓存读写异常时,忽略异常
// * @author YC104
// */
//@Slf4j
//public class IgnoreExceptionCacheErrorHandler implements CacheErrorHandler {
//
//
//	@Override
//	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
//		log.error(exception.getMessage(), exception);
//	}
//
//	@Override
//	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
//		log.error(exception.getMessage(), exception);
//	}
//
//	@Override
//	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
//		log.error(exception.getMessage(), exception);
//	}
//
//	@Override
//	public void handleCacheClearError(RuntimeException exception, Cache cache) {
//		log.error(exception.getMessage(), exception);
//	}
//}
