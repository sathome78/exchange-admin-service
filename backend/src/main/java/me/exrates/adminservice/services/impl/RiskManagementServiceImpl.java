package me.exrates.adminservice.services.impl;

import me.exrates.adminservice.core.repository.CoreExorderRepository;
import me.exrates.adminservice.domain.api.RiskManagementBoardDTO;
import me.exrates.adminservice.services.RiskManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RiskManagementServiceImpl implements RiskManagementService {

    private final CoreExorderRepository coreExorderRepository;

    @Autowired
    public RiskManagementServiceImpl(CoreExorderRepository coreExorderRepository) {
        this.coreExorderRepository = coreExorderRepository;
    }

    @Override
    public RiskManagementBoardDTO getRiskManagementBoard() {
        RiskManagementBoardDTO dto = new RiskManagementBoardDTO();
        final Map<String, Integer> dailyBuySellVolume = coreExorderRepository.getDailyBuySellVolume();
        dto.setDiagramBuyPercentage(dailyBuySellVolume.get("BUY"));
        dto.setDiagramSellPercentage(dailyBuySellVolume.get("SELL"));

        return dto;
    }
}
