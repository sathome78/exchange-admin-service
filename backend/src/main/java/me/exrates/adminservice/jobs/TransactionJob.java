package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.services.SyncTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Log4j2
@Component
@Profile("!light")
public class TransactionJob {

    private final SyncTransactionService syncTransactionService;

    @Autowired
    public TransactionJob(SyncTransactionService syncTransactionService) {
        this.syncTransactionService = syncTransactionService;
    }

    @Scheduled(cron = "${scheduled.update.sync-transactions}")
    public void update() {
        syncTransactionService.syncTransactions();
    }
}