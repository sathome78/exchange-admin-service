package me.exrates.adminservice.events.listeners;

import me.exrates.adminservice.events.TransactionsUpdateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TransactionsUpdateEventListener {


    @Async("threadPoolTaskExecutor")
    @EventListener
    public void handleTransactionsUpdateEvent(TransactionsUpdateEvent event) {
        String message = "Updated transactions for users with ids: " + event.getUpdatedUserIds();
        System.out.println(message);
    }

}
