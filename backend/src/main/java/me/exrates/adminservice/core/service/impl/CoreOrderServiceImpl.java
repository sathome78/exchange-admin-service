package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreOrderDto;
import me.exrates.adminservice.core.repository.CoreOrderRepository;
import me.exrates.adminservice.core.service.CoreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static me.exrates.adminservice.configurations.CacheConfiguration.ORDER_CACHE_BY_ID;

@Log4j2
@Service
@Transactional
public class CoreOrderServiceImpl implements CoreOrderService {

    private final CoreOrderRepository coreOrderRepository;
    private final Cache orderCacheById;

    @Autowired
    public CoreOrderServiceImpl(CoreOrderRepository coreOrderRepository,
                                @Qualifier(ORDER_CACHE_BY_ID) Cache orderCacheById) {
        this.coreOrderRepository = coreOrderRepository;
        this.orderCacheById = orderCacheById;
    }

    @Transactional(readOnly = true)
    @Override
    public CoreOrderDto findCachedOrderById(int id) {
        return orderCacheById.get(id, () -> coreOrderRepository.findOrderById(id));
    }
}