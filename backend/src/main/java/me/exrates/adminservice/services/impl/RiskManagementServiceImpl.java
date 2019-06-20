package me.exrates.adminservice.services.impl;

import me.exrates.adminservice.core.repository.CoreExorderRepository;
import me.exrates.adminservice.domain.api.RiskManagementBoardDTO;
import me.exrates.adminservice.services.RiskManagementService;
import me.exrates.adminservice.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class RiskManagementServiceImpl implements RiskManagementService {

    private final CoreExorderRepository coreExorderRepository;
    private final TransactionService transactionService;

    @Autowired
    public RiskManagementServiceImpl(CoreExorderRepository coreExorderRepository,
                                     TransactionService transactionService) {
        this.coreExorderRepository = coreExorderRepository;
        this.transactionService = transactionService;
    }

    @Override
    public RiskManagementBoardDTO getRiskManagementBoard() {
        final Map<String, Integer> dailyBuySellVolume = coreExorderRepository.getDailyBuySellVolume();
        final Map<String, BigDecimal> dailyCommissionRevenue = transactionService.getDailyCommissionRevenue();
        final Map<String, BigDecimal> dailyInnerTradeVolume = transactionService.getDailyInnerTradeVolume();

        return RiskManagementBoardDTO.builder()
                .diagramSellPercentage(dailyBuySellVolume.getOrDefault("buy", 0))
                .diagramBuyPercentage(dailyBuySellVolume.getOrDefault("sell", 0))
                // bot api
                .tradeBotCoverageBTC(BigDecimal.ZERO)
                .tradeBotCoverageUSD(BigDecimal.ZERO)
                .innerTradeVolumeDayBTC(dailyInnerTradeVolume.getOrDefault("BTC", BigDecimal.ZERO))
                .innerTradeVolumeDayUSD(dailyInnerTradeVolume.getOrDefault("USD", BigDecimal.ZERO))
                // bot api
                .outerCommissionBTC(BigDecimal.ZERO)
                .outerCommissionUSD(BigDecimal.ZERO)
                // needs resolving
                .tradeIncomeBTC(BigDecimal.ZERO)
                .tradeIncomeUSD(BigDecimal.ZERO)
                .commissionRevenueBTC(dailyCommissionRevenue.getOrDefault("BTC", BigDecimal.ZERO))
                .commissionRevenueUSD(dailyCommissionRevenue.getOrDefault("USD", BigDecimal.ZERO))
                .uniqueClientsQuantity(coreExorderRepository.getDailyUniqueUsersQuantity())
                .build();
    }
}
