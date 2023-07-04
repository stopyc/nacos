/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.nacos.naming.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: rabbit-mq
 * @description: 14
 * @author: stop.yc
 * @create: 2023-02-14 17:40
 **/
@Configuration
@Slf4j
public class RabbitMqConfig {

    /**
     * 消息发送的主交换机
     */
    public static final String EXCHANGE_NAME = "boot_topic_exchange";

    /**
     * 死信交换机
     */
    public static final String DLX_EXCHANGE_NAME = "dlx_topic_exchange";

    /**
     * 主消息队列
     */
    public static final String QUEUE_NAME = "boot_queue";

    /**
     * 死信队列
     */
    public static final String DLX_QUEUE_NAME = "dlx_queue";

    @Value("${RABBIT_HOST}")
    private String rabbitMqHost;

    @Value("${RABBIT_USERNAME}")
    private String rabbitMqUsername;

    @Value("${RABBIT_PASSWORD}")
    private String rabbitMqPassword;


    /**
     * 回调
     * @param myConnectionFactory :连接工厂
     * @return :监听器回调工厂
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory myConnectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(myConnectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMaxConcurrentConsumers(20);

        return factory;
    }

    /**
     * 连接工厂
     * @return :连接
     */
    @Bean
    public ConnectionFactory myConnectionFactory () {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMqHost);
        connectionFactory.setPassword(rabbitMqUsername);
        connectionFactory.setUsername(rabbitMqPassword);
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return connectionFactory;
    }


    /**
     * mq模板
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory myConnectionFactory) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(myConnectionFactory);

        rabbitTemplate.setMandatory(true);


        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {

            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                String exchange1 = returnedMessage.getExchange();
                String replyText = returnedMessage.getReplyText();
                String routingKey1 = returnedMessage.getRoutingKey();
                int replyCode = returnedMessage.getReplyCode();
                String message1 = new String(returnedMessage.getMessage().getBody());
                log.error("FailedToSendMessageToQueue exchange:{},replyText:{},routingKey:{},replyCode:{},message:{}"
                        , exchange1, replyText, routingKey1, replyCode, message1);
            }
        });



        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                //成功
            } else {
                log.error("The message failed to be sent to the switch due to the following error:: {}", cause);
            }
        });
        return rabbitTemplate;
    }




    /**
     *
     *主
     * @return :交换
     */
    @Bean("bootExchange")
    public Exchange bootExchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    /**
     * 主
     *
     * @return :队列
     */
    @Bean("bootQueue")
    public Queue bootQueue() {

        return QueueBuilder

                .durable(QUEUE_NAME)

                .withArgument("x-message-ttl", 20000L)

                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)

                .withArgument("x-dead-letter-routing-key", "dlx.dead")

                .withArgument("x-max-length", 20L)
                .build();

    }

    /**
     * bind
     *
     * @param bootQueue    :队
     * @param bootExchange :jiaohuanji
     * @return :bind
     */
    @Bean
    public Binding bindQueueExchange(@Qualifier("bootQueue") Queue bootQueue, @Qualifier("bootExchange") Exchange bootExchange) {
        return BindingBuilder.bind(bootQueue).to(bootExchange).with("boot.#").noargs();
    }


    /**
     * 死
     *
     * @return :交换机
     */
    @Bean("dlxExchange")
    public Exchange dlxExchange() {
        return ExchangeBuilder.topicExchange(DLX_EXCHANGE_NAME).durable(true).build();
    }


    /**
     * 死
     * @return :队列
     */
    @Bean("dlxQueue")
    public Queue dixQueue() {


        return QueueBuilder
                .durable(DLX_QUEUE_NAME)
                .withArgument("x-message-ttl", 30000L)
                .build();
    }


    /**
     * bind
     *
     * @param dlxQueue    :交换机
     * @param dlxExchange :对象
     * @return :bind
     */
    @Bean
    public Binding bindDLXQueueExchange(@Qualifier("dlxQueue") Queue dlxQueue, @Qualifier("dlxExchange") Exchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with("dlx.#").noargs();
    }
}
