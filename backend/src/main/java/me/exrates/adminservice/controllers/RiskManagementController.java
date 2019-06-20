package me.exrates.adminservice.controllers;

import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.RiskManagementBoardDTO;
import me.exrates.adminservice.domain.api.UserInsightDTO;
import me.exrates.adminservice.services.RiskManagementService;
import me.exrates.adminservice.services.UserInsightsService;
import me.exrates.adminservice.utils.AppConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/risks/management", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RiskManagementController {

    private final UserInsightsService userInsightsService;
    private final RiskManagementService riskManagementService;

    @Autowired
    public RiskManagementController(UserInsightsService userInsightsService,
                                    RiskManagementService riskManagementService) {
        this.userInsightsService = userInsightsService;
        this.riskManagementService = riskManagementService;
    }

    // /api/risks/management/table
    @GetMapping("/table")
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
            limit = AppConstants.checkLimit(limit);
            offset = AppConstants.checkOffset(offset);
            return userInsightsService.findAll(limit, offset);
        }
    }

    // /api/risks/management/board
    @GetMapping("/board")
    @ResponseBody
    public RiskManagementBoardDTO getRiskManagementBoard() {
        return riskManagementService.getRiskManagementBoard();
    }

    @GetMapping("/test")
    @ResponseBody
    public RiskManagementBoardDTO test() {
        return RiskManagementBoardDTO.builder()
                .tradeBotCoverageBTC(BigDecimal.valueOf(1.00001))
                .tradeBotCoverageUSD(BigDecimal.valueOf(9003))
                .build();
    }
}
