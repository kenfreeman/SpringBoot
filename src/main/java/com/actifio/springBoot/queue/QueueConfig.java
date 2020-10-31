package com.actifio.springBoot.queue;

/**
 * Copyright (c) 2020 Actifio Inc. All Rights Reserved
 * <p/>
 * This program code contains trade secrets of Actifio Inc. that
 * (i) derive independent economic value, actual or potential, from not being
 * generally known to, and not being readily ascertainable by proper means by
 * other persons who can obtain economic value from their disclosure or use and;
 * (ii) are the subject of efforts by Actifio Inc that are reasonable under the
 * circumstances to maintain their secrecy. You may not use this program code
 * without prior written authorization from Actifio Inc.
 * <p/>
 * =============================================================
 */
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class QueueConfig {
    @Value("${activemq.brokerUrl}")
    private String brokerUrl;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);

        return activeMQConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory());
        factory.setConcurrency("1");
        return factory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(activeMQConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }

    @Bean
    public QueueService queue() {
        return new QueueService();
    }
}
