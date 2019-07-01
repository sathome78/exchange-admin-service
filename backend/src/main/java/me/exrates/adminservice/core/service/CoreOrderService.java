package me.exrates.adminservice.core.service;

import me.exrates.adminservice.core.domain.CoreOrderDto;

public interface CoreOrderService {

    CoreOrderDto findCachedOrderById(int id);
}