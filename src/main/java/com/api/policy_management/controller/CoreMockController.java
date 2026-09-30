package com.api.policy_management.controller;

import com.api.policy_management.dto.CoreEventRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void receiveEvent(@Valid @RequestBody CoreEventRequest request) {
        log.info("Operación intentada enviar al CORE | evento={} | polizaId={}",
                request.evento(), request.polizaId());
    }
}
