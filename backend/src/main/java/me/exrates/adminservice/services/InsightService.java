package me.exrates.adminservice.services;

import me.exrates.adminservice.domain.PagedResult;

import java.util.Set;

public interface InsightService<T> {

    PagedResult<T> findAll(int limit, int offset);

    PagedResult<T> findAll(String username);

    PagedResult<T> findAll(int userId);

    void reloadCache(Set<Integer> userIds);
}
