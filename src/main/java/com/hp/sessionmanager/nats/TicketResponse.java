package com.hp.sessionmanager.nats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketResponse {

    public void receiverResponse(String message) {
        log.info("Response Message is", message);

    }
}
