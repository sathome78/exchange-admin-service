package me.exrates.adminservice.controllers;

import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.UserInsightDTO;
import me.exrates.adminservice.services.UserInsightsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(value = "/api/risks", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RiskManagementController {

    private final UserInsightsService userInsightsService;

    @Autowired
    public RiskManagementController(UserInsightsService userInsightsService) {
        this.userInsightsService = userInsightsService;
    }

    // /api/risks/management/table
    @GetMapping("/management/table")
    @ResponseBody
    public PagedResult<UserInsightDTO> getRiskManagementTable(@RequestParam(required = false) Integer limit,
                                                              @RequestParam(required = false) Integer offset,
                                                              @RequestParam(required = false) Integer userId,
                                                              @RequestParam(required = false) String username) {
        if (StringUtils.isNotEmpty(username)) {
            return userInsightsService.findAll(username);
        } else if (Objects.nonNull(userId) && userId > 0) {
            return userInsightsService.findAll(userId);
        } else {
            return userInsightsService.findAll(limit, offset);
        }
    }

}
