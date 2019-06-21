package me.exrates.adminservice.core.service.impl;

import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.exceptions.WalletPersistException;
import me.exrates.adminservice.core.repository.CoreCompanyWalletRepository;
import me.exrates.adminservice.core.service.CoreCompanyWalletService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CoreCompanyWalletServiceImplTest {

    @Mock
    private CoreCompanyWalletRepository coreCompanyWalletRepository;

    private CoreCompanyWalletService coreCompanyWalletService;

    @Before
    public void setUp() throws Exception {
        coreCompanyWalletService = spy(new CoreCompanyWalletServiceImpl(coreCompanyWalletRepository));
    }

    @Test
    public void findByCurrency_ok() {
        doReturn(CoreCompanyWalletDto.builder().build())
                .when(coreCompanyWalletRepository)
                .findByCurrency(any(CoreCurrencyDto.class));

        CoreCompanyWalletDto companyWallet = coreCompanyWalletService.findByCurrency(CoreCurrencyDto.builder().build());

        assertNotNull(companyWallet);

        verify(coreCompanyWalletRepository, atLeastOnce()).findByCurrency(any(CoreCurrencyDto.class));
    }

    @Test
    public void findByCurrency_not_found() {
        doReturn(null)
                .when(coreCompanyWalletRepository)
                .findByCurrency(any(CoreCurrencyDto.class));

        CoreCompanyWalletDto companyWallet = coreCompanyWalletService.findByCurrency(CoreCurrencyDto.builder().build());

        assertNull(companyWallet);

        verify(coreCompanyWalletRepository, atLeastOnce()).findByCurrency(any(CoreCurrencyDto.class));
    }

    @Test
    public void deposit_ok() {
        doReturn(true)
                .when(coreCompanyWalletRepository)
                .update(any(CoreCompanyWalletDto.class));

        coreCompanyWalletService.deposit(CoreCompanyWalletDto.builder()
                .balance(BigDecimal.TEN)
                .commissionBalance(BigDecimal.ONE)
                .build(), BigDecimal.TEN, BigDecimal.ONE);

        verify(coreCompanyWalletRepository, atLeastOnce()).update(any(CoreCompanyWalletDto.class));
    }

    @Test(expected = WalletPersistException.class)
    public void deposit_error() {
        doReturn(false)
                .when(coreCompanyWalletRepository)
                .update(any(CoreCompanyWalletDto.class));

        coreCompanyWalletService.deposit(CoreCompanyWalletDto.builder()
                .balance(BigDecimal.TEN)
                .commissionBalance(BigDecimal.ONE)
                .build(), BigDecimal.TEN, BigDecimal.ONE);

        verify(coreCompanyWalletRepository, atLeastOnce()).update(any(CoreCompanyWalletDto.class));
    }
}