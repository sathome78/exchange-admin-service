package me.exrates.adminservice.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface OrderService {

    void syncOrders();

    Map<Integer, List<Integer>> getAllUserClosedOrders(Collection<Integer> userIds);

//    Map<String, BigDecimal> getDailyCommissionRevenue();
//
//    Map<String, BigDecimal> getDailyInnerTradeVolume();
//
//    Map<Integer, List<Integer>> getAllUserClosedOrders(Collection<Integer> usersIds);
}
