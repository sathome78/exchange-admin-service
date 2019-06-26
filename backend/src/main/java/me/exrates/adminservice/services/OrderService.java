package me.exrates.adminservice.services;

import me.exrates.adminservice.domain.ClosedOrder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface OrderService {

    void syncOrders();

    Map<Integer, List<ClosedOrder>> findAllUserClosedOrders(Collection<Integer> userIds);

//    Map<String, BigDecimal> getDailyCommissionRevenue();
//
//    Map<String, BigDecimal> getDailyInnerTradeVolume();
//
//    Map<Integer, List<Integer>> findAllUserClosedOrders(Collection<Integer> usersIds);
}
