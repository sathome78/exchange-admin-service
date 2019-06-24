package me.exrates.adminservice.core.service.impl;

import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.CoreWalletOperationDto;
import me.exrates.adminservice.core.domain.enums.WalletTransferStatus;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.core.service.CoreWalletService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CoreWalletServiceImplTest {

    @Mock
    private CoreWalletRepository coreWalletRepository;

    private CoreWalletService coreWalletService;

    @Before
    public void setUp() throws Exception {
        coreWalletService = spy(new CoreWalletServiceImpl(coreWalletRepository));
    }

    @Test
    public void findByUserAndCurrency_ok() {
        doReturn(CoreWalletDto.builder()
                .userId(1)
                .currencyId(4)
                .build())
                .when(coreWalletRepository)
                .findByUserAndCurrency(anyInt(), anyInt());

        CoreWalletDto wallet = coreWalletService.findByUserAndCurrency(1, 4);

        assertNotNull(wallet);
        assertEquals(1, wallet.getUserId());
        assertEquals(4, wallet.getCurrencyId());

        verify(coreWalletRepository, atLeastOnce()).findByUserAndCurrency(anyInt(), anyInt());
    }

    @Test
    public void findByUserAndCurrency_not_found() {
        doReturn(null)
                .when(coreWalletRepository)
                .findByUserAndCurrency(anyInt(), anyInt());

        CoreWalletDto wallet = coreWalletService.findByUserAndCurrency(1, 4);

        assertNull(wallet);

        verify(coreWalletRepository, atLeastOnce()).findByUserAndCurrency(anyInt(), anyInt());
    }

    @Test
    public void isUserAllowedToManuallyChangeWalletBalance_ok() {
        doReturn(true)
                .when(coreWalletRepository)
                .isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());

        boolean allowed = coreWalletService.isUserAllowedToManuallyChangeWalletBalance(1, 4);

        assertTrue(allowed);

        verify(coreWalletRepository, atLeastOnce()).isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
    }

    @Test
    public void walletBalanceChange_ok() {
        doReturn(WalletTransferStatus.SUCCESS)
                .when(coreWalletRepository)
                .walletBalanceChange(any(CoreWalletOperationDto.class));

        WalletTransferStatus status = coreWalletService.walletBalanceChange(CoreWalletOperationDto.builder().build());

        assertNotNull(status);
        assertEquals(WalletTransferStatus.SUCCESS, status);

        verify(coreWalletRepository, atLeastOnce()).walletBalanceChange(any(CoreWalletOperationDto.class));
    }
}