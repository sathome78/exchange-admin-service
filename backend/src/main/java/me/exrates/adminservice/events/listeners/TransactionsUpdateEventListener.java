package me.exrates.adminservice.events.listeners;

import me.exrates.adminservice.events.TransactionsUpdateEvent;
import me.exrates.adminservice.services.UserInsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TransactionsUpdateEventListener {

    private final UserInsightService userInsightService;

    @Autowired
    public TransactionsUpdateEventListener(UserInsightService userInsightsService) {
        this.userInsightService = userInsightsService;
    }


    @Async("threadPoolTaskExecutor")
    @EventListener
    public void handleTransactionsUpdateEvent(TransactionsUpdateEvent event) {
        userInsightService.reloadCache(event.getUpdatedUserIds());
    }

}
