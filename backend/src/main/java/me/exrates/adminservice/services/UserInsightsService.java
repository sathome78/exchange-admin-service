package me.exrates.adminservice.services;

import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.UserInsightDTO;

public interface UserInsightsService {

    PagedResult<UserInsightDTO> findAll(int limit, int offset);

    PagedResult<UserInsightDTO> findAll(String username);

    PagedResult<UserInsightDTO> findAll(int userId);


}
