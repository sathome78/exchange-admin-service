package me.exrates.adminservice.events.listeners;

import me.exrates.adminservice.events.TransactionsUpdateEvent;
import me.exrates.adminservice.services.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TransactionsUpdateEventListener {

    private final InsightService userInsightsService;

    @Autowired
    public TransactionsUpdateEventListener(InsightService userInsightsService) {
        this.userInsightsService = userInsightsService;
    }


    @Async("threadPoolTaskExecutor")
    @EventListener
    public void handleTransactionsUpdateEvent(TransactionsUpdateEvent event) {
        userInsightsService.reloadCache(event.getUpdatedUserIds());
    }

}
