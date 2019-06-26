package me.exrates.adminservice.jobs;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.services.OrderService;
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
public class ClosedOrderJob {

    private final OrderService orderService;

    @Autowired
    public ClosedOrderJob(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(cron = "${scheduled.update.sync-transactions}")
    public void update() {
        try {
            orderService.syncOrders();
        } catch (Exception ex) {
            log.error("--> In processing 'ClosedOrderJob' occurred error", ex);
        }
    }
}
