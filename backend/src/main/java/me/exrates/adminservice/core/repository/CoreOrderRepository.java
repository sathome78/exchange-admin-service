package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.domain.ClosedOrder;

import java.util.List;

public interface CoreOrderRepository {

    List<ClosedOrder> findAllLimited(int chunkSize, int maxId);

}
