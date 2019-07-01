package me.exrates.adminservice.controllers;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.service.CoreCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = "/api/currency", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CurrencyController {

    private final CoreCurrencyService coreCurrencyService;

    @Autowired
    public CurrencyController(CoreCurrencyService coreCurrencyService) {
        this.coreCurrencyService = coreCurrencyService;
    }

    @GetMapping("/retrieve/active-currencies")
    public ResponseEntity<List<CoreCurrencyDto>> retrieveDashboardOne() {
        return ResponseEntity.ok(coreCurrencyService.getCachedActiveCurrencies());
    }
}