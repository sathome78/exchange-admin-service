package me.exrates.adminservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionsUpdateEvent {
    private final boolean updated;
}
