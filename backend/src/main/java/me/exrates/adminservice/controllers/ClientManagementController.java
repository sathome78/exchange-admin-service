package me.exrates.adminservice.controllers;

import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.ClientInsightDTO;
import me.exrates.adminservice.domain.api.ClientManagementBoardDTO;
import me.exrates.adminservice.services.ClientInsightService;
import me.exrates.adminservice.services.ClientManagementService;
import me.exrates.adminservice.utils.AppConstants;
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
@RequestMapping(value = "/api/risks/clients/management", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ClientManagementController {

    private final ClientInsightService clientInsightService;
    private final ClientManagementService clientManagementService;

    @Autowired
    public ClientManagementController(ClientInsightService clientInsightService,
                                      ClientManagementService clientManagementService) {
        this.clientInsightService = clientInsightService;
        this.clientManagementService = clientManagementService;
    }

    // /api/risks/clients/management/table
    @GetMapping("/table")
    @ResponseBody
    public PagedResult<ClientInsightDTO> getRiskManagementTable(@RequestParam(required = false) Integer limit,
                                                                @RequestParam(required = false) Integer offset,
                                                                @RequestParam(required = false) Integer userId,
                                                                @RequestParam(required = false) String username) {
        if (StringUtils.isNotEmpty(username)) {
            return clientInsightService.findAll(username);
        } else if (Objects.nonNull(userId) && userId > 0) {
            return clientInsightService.findAll(userId);
        } else {
            limit = AppConstants.checkLimit(limit);
            offset = AppConstants.checkOffset(offset);
            return clientInsightService.findAll(limit, offset);
        }
    }

    // /api/risks/clients/management/board
    @GetMapping("/board")
    @ResponseBody
    public ClientManagementBoardDTO getRiskManagementBoard() {
        return clientManagementService.getClientManagementBoard();
    }

}
