package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.services.TransactionService;
import me.exrates.adminservice.utils.NonDevelopmentCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Log4j2
@Component
@Conditional(NonDevelopmentCondition.class)
public class TransactionJob {

    private final TransactionService syncTransactionService;

    @Autowired
    public TransactionJob(TransactionService syncTransactionService) {
        this.syncTransactionService = syncTransactionService;
    }

    @Scheduled(cron = "${scheduled.update.sync-transactions}")
    public void update() {
        try {
            syncTransactionService.syncTransactions();
        } catch (Exception ex) {
            log.info("--> In processing 'TransactionJob' occurred error", ex);
        }
    }
}
