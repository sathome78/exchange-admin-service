package me.exrates.adminservice.controllers;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.BalancesDto;
import me.exrates.adminservice.domain.DashboardOneDto;
import me.exrates.adminservice.domain.DashboardTwoDto;
import me.exrates.adminservice.domain.ExternalReservedWalletAddressDto;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.nonNull;

@Log4j2
@RestController
@RequestMapping(value = "/api/financial-monitoring", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class FinancialMonitoringController {

    private final WalletService walletService;

    @Autowired
    public FinancialMonitoringController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/external-wallets")
    public ResponseEntity<PagedResult<ExternalWalletBalancesDto>> retrieveExternalWalletBalances(@RequestParam(required = false, defaultValue = "20") Integer limit,
                                                                                                 @RequestParam(required = false, defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(walletService.getExternalWalletBalances(limit, offset));
    }

    @GetMapping("/dashboard-one")
    public ResponseEntity<DashboardOneDto> retrieveDashboardOne() {
        return ResponseEntity.ok(walletService.getDashboardOne());
    }

    @GetMapping("/external-wallets/summary/{ticker}")
    public ResponseEntity<BigDecimal> retrieveSummary(@PathVariable("ticker") String ticker) {
        BigDecimal summary;
        switch (ticker) {
            case "USD":
                summary = walletService.retrieveSummaryUSD();
                break;
            case "BTC":
                summary = walletService.retrieveSummaryBTC();
                break;
            default:
                summary = BigDecimal.ZERO;
                break;
        }
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/reserved-wallets")
    public ResponseEntity createWalletAddress(@RequestParam int currencyId) {
        walletService.createWalletAddress(currencyId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reserved-wallets")
    public ResponseEntity deleteWalletAddress(@RequestParam int id,
                                              @RequestParam int currencyId,
                                              @RequestParam String walletAddress) {
        walletService.deleteWalletAddress(id, currencyId, walletAddress);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/reserved-wallets/as-address")
    public ResponseEntity submitWalletAddressAsAddress(@RequestParam int id,
                                                       @RequestParam int currencyId,
                                                       @RequestParam String walletAddress) {
        final BigDecimal reservedWalletBalance = walletService.getExternalReservedWalletBalance(currencyId, walletAddress);
        if (nonNull(reservedWalletBalance)) {
            ExternalReservedWalletAddressDto externalReservedWalletAddressDto = ExternalReservedWalletAddressDto.builder()
                    .id(id)
                    .currencyId(currencyId)
                    .walletAddress(walletAddress)
                    .balance(reservedWalletBalance)
                    .build();
            walletService.updateWalletAddress(externalReservedWalletAddressDto, true);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reserved-wallets/as-name")
    public ResponseEntity submitWalletAddressAsName(@RequestParam int id,
                                                    @RequestParam int currencyId,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam String walletAddress,
                                                    @RequestParam BigDecimal reservedWalletBalance) {
        ExternalReservedWalletAddressDto externalReservedWalletAddressDto = ExternalReservedWalletAddressDto.builder()
                .id(id)
                .name(name)
                .currencyId(currencyId)
                .walletAddress(walletAddress)
                .balance(reservedWalletBalance)
                .build();
        walletService.updateWalletAddress(externalReservedWalletAddressDto, false);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/reserved-wallets/{currency_id}")
    public ResponseEntity<List<ExternalReservedWalletAddressDto>> getReservedWallets(@PathVariable("currency_id") String currencyId) {
        return ResponseEntity.ok(walletService.getReservedWalletsByCurrencyId(currencyId));
    }

    @PutMapping("/accounting-imbalance")
    public ResponseEntity submitAccountingImbalance(@RequestParam String currencyName,
                                                    @RequestParam(defaultValue = "0") BigDecimal accountingProfit,
                                                    @RequestParam(defaultValue = "0") BigDecimal accountingManualBalanceChanges) {
        walletService.updateAccountingImbalance(currencyName, accountingProfit, accountingManualBalanceChanges);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/sign-of-monitoring")
    public ResponseEntity updateSignOfMonitoringForCurrency(@RequestParam int currencyId,
                                                            @RequestParam boolean signOfMonitoring) {
        walletService.updateSignOfMonitoringForCurrency(currencyId, signOfMonitoring);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/monitoring-range")
    public ResponseEntity updateMonitoringRangeForCurrency(@RequestParam int currencyId,
                                                           @RequestParam BigDecimal coinRange,
                                                           @RequestParam boolean checkByCoinRange,
                                                           @RequestParam BigDecimal usdRange,
                                                           @RequestParam boolean checkByUsdRange) {
        walletService.updateMonitoringRangeForCurrency(currencyId, coinRange, checkByCoinRange, usdRange, checkByUsdRange);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/balances-slice")
    public ResponseEntity<PagedResult<BalancesDto>> getBalancesSliceStatistic(@RequestParam(required = false) List<String> currencyNames,
                                                                              @RequestParam(required = false) BigDecimal minExBalance,
                                                                              @RequestParam(required = false) BigDecimal maxExBalance,
                                                                              @RequestParam(required = false) BigDecimal minInBalance,
                                                                              @RequestParam(required = false) BigDecimal maxInBalance,
                                                                              @RequestParam(required = false, defaultValue = "20") Integer limit,
                                                                              @RequestParam(required = false, defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(walletService.getBalancesSliceStatistic(currencyNames, minExBalance, maxExBalance, minInBalance, maxInBalance, limit, offset));
    }

    @GetMapping("/dashboard-two")
    public ResponseEntity<DashboardTwoDto> getDashboardTwo(@RequestParam(required = false) List<String> currencyNames,
                                                           @RequestParam(required = false) BigDecimal minExBalance,
                                                           @RequestParam(required = false) BigDecimal maxExBalance,
                                                           @RequestParam(required = false) BigDecimal minInBalance,
                                                           @RequestParam(required = false) BigDecimal maxInBalance) {
        return ResponseEntity.ok(walletService.getDashboardTwo(currencyNames, minExBalance, maxExBalance, minInBalance, maxInBalance));
    }
}