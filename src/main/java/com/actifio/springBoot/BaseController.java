package com.actifio.springBoot;

import com.actifio.springBoot.model.Ticket;
import com.actifio.springBoot.queue.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

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
@SuppressWarnings("unused")
@org.springframework.stereotype.Controller
@EnableAutoConfiguration
public class BaseController
{
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private QueueService queueService;

    @Value("${queue.name}")
    private String queueName;

    @Value("${worker.name}")
    private String workerName;

    @Value("${store.enabled}")
    private boolean storeEnabled;

    @Value("${worker.enabled}")
    private boolean workerEnabled;

    @GetMapping("/")
    public String home(Model model) {
        int pendingMessages = queueService.pendingJobs(queueName);
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("pendingJobs", pendingMessages);
        model.addAttribute("completedJobs", queueService.completedJobs());
        model.addAttribute("isConnected", queueService.isUp() ? "yes" : "no");
        model.addAttribute("queueName", this.queueName);
        model.addAttribute("workerName", this.workerName);
        model.addAttribute("isStoreEnabled", this.storeEnabled);
        // model.addAttribute("isWorkerEnabled", this.workerEnabled);
        model.addAttribute("isWorkerEnabled", Boolean.TRUE);
        return "home";
    }

    @PostMapping("/submit")
    public String submit(@ModelAttribute Ticket ticket) {
        for (long i = 0; i < ticket.getQuantity(); i++) {
            String id = UUID.randomUUID().toString();
            queueService.send(queueName, id);
        }
        return "success";
    }

    @ResponseBody
    @RequestMapping(value="/metrics", produces="text/plain")
    public String metrics() {
        int totalMessages = queueService.pendingJobs(queueName);
        return "# HELP messages Number of messages in the queueService\n"
                + "# TYPE messages gauge\n"
                + "messages " + totalMessages;
    }

    @RequestMapping(value="/health")
    public ResponseEntity health() {
        HttpStatus status;
        if (queueService.isUp()) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(status);
    }

}
