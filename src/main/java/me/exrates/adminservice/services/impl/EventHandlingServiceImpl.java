package me.exrates.adminservice.service.impl;

import me.exrates.adminservice.events.TransactionsUpdateEvent;
import me.exrates.adminservice.service.EventHandlingService;
import me.exrates.adminservice.service.UpdateTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventHandlingServiceImpl implements EventHandlingService {

    private final UpdateTransactionService updateTransactionService;

    @Autowired
    public EventHandlingServiceImpl(UpdateTransactionService updateTransactionService) {
        this.updateTransactionService = updateTransactionService;
    }

    @Override
    public void onTransactionsUpdate(TransactionsUpdateEvent event) {
        if (event.isUpdated()) {
//            todo please recalculate scores
            updateTransactionService.onUpdate();
        }
    }
}
