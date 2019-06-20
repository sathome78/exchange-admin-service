package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.service.SyncUserService;
import me.exrates.adminservice.utils.NonDevelopmentCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Log4j2
@Component
@Conditional(NonDevelopmentCondition.class)
public class SyncUsersJob {

    private final SyncUserService syncUserService;

    @Autowired
    public SyncUsersJob(SyncUserService syncUserService) {
        this.syncUserService = syncUserService;
    }

    @Scheduled(cron = "${scheduled.update.sync-users}")
    public void update() {
        try {
            syncUserService.syncUsers();
        } catch (Exception ex) {
            log.info("--> In processing 'SyncUsersJob' occurred error", ex);
        }
    }
}
