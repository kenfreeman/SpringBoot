package com.actifio.springBoot;

import com.actifio.springBoot.queue.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

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
@SpringBootApplication
public class SpringBoot implements JmsListenerConfigurer
{
    @Value("${queue.name}")
    private String queueName;

    @Value("${worker.name}")
    private String workerName;

    @Value("${worker.enabled}")
    private boolean workerEnabled;

    @Autowired
    private QueueService queueService;

    private String[] args;
    private static final String PORT_PROPERTY = "server.port";
    @SuppressWarnings("FieldCanBeLocal")
    private static boolean connected = false;

    public SpringBoot() {
    }

    private SpringBoot(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) {
        System.setProperty(PORT_PROPERTY, "8888");
        new SpringBoot(args).run();
    }

    private void run()
    {
        String port = System.getProperty(PORT_PROPERTY);

        SpringApplication.run(this.getClass(), args);
        System.out.println("Web server is running on port " + port);
        connected = true;
    }

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        if (workerEnabled) {
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            endpoint.setId(workerName);
            endpoint.setDestination(queueName);
            endpoint.setMessageListener(queueService);
            registrar.registerEndpoint(endpoint);
        }
    }
}
