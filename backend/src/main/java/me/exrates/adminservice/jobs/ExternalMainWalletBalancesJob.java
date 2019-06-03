package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@EnableScheduling
@Component
@Profile("!light")
public class ExternalMainWalletBalancesJob {

    private final WalletService walletService;

    @Autowired
    public ExternalMainWalletBalancesJob(WalletService walletService) {
        this.walletService = walletService;
    }

    @Scheduled(cron = "${scheduled.update.external-balances}")
    public void update() {
        walletService.updateExternalMainWalletBalances();
    }
}
