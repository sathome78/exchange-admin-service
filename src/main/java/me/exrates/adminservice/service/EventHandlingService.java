package me.exrates.adminservice.service;

import me.exrates.adminservice.events.TransactionsUpdateEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

public interface EventHandlingService {

    @Async
    @TransactionalEventListener
    void onTransactionsUpdate(TransactionsUpdateEvent event);
}
