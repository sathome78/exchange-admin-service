package me.exrates.adminservice.events;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Data
public class TransactionsUpdateEvent extends ApplicationEvent {

    private Set<Integer> updatedUserIds;

    public TransactionsUpdateEvent(Object source, Set<Integer> updatedUserIds) {
        super(source);
        this.updatedUserIds = updatedUserIds;
    }
}
