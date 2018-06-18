package com.hybris.yps.cloud.controller;

import de.hybris.platform.core.suspend.SuspendResumeService;
import de.hybris.platform.core.suspend.SystemStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("healthz")
public class LivenessController {
    @Resource
    private SuspendResumeService suspendResumeService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> getLiveness() {
        HttpStatus status = HttpStatus.OK;

        SystemStatus systemStatus = suspendResumeService.getSystemStatus();
        if (!SystemStatus.RUNNING.equals(systemStatus)) {
            status = HttpStatus.GONE;
        }
        return ResponseEntity.status(status).body(systemStatus.name());
    }
}
