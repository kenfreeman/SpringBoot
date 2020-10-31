package com.actifio.springBoot.queue;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.jms.core.JmsTemplate;


import javax.jms.*;
import java.util.Collections;

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
@Component
public class QueueService implements MessageListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    private int counter = 0;

    public int completedJobs() {
        return counter;
    }

    public void send(String destination, String message) {
        LOGGER.info("sending message='{}' to destination='{}'", message, destination);
        jmsTemplate.convertAndSend(destination, message);
    }

    public int pendingJobs(String queueName) {
        return jmsTemplate.browse(queueName, (s, qb) -> Collections.list(qb.getEnumeration()).size());
    }

    public boolean isUp() {
        ConnectionFactory connection = jmsTemplate.getConnectionFactory();
        try {
            connection.createConnection().close();
            return true;
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onMessage(javax.jms.Message message) {
        if (message instanceof ActiveMQTextMessage) {
            ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
            try {
                LOGGER.info("Processing task " + textMessage.getText());
                Thread.sleep(5000);
                LOGGER.info("Completed task " + textMessage.getText());
            }
            catch (InterruptedException | JMSException e) {
                e.printStackTrace();
            }
            counter++;
        }
        else {
            LOGGER.error("Message is not a text message " + message.toString());
        }
    }
}
