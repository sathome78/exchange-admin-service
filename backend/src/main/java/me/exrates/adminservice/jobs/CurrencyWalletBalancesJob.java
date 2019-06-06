package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.services.WalletBalancesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@EnableScheduling
@Component
@Profile("!light")
public class CurrencyWalletBalancesJob {

    private final WalletBalancesService walletBalancesService;

    @Autowired
    public CurrencyWalletBalancesJob(WalletBalancesService walletBalancesService) {
        this.walletBalancesService = walletBalancesService;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1000)
    public void update() {
        walletBalancesService.updateCurrencyBalances();
    }
}
