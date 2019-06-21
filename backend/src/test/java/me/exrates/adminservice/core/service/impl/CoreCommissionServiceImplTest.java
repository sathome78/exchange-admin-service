package me.exrates.adminservice.core.service.impl;

import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.core.exceptions.CommissionsNotFoundException;
import me.exrates.adminservice.core.repository.CoreCommissionRepository;
import me.exrates.adminservice.core.service.CoreCommissionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CoreCommissionServiceImplTest {

    @Mock
    private CoreCommissionRepository coreCommissionRepository;

    private CoreCommissionService coreCommissionService;

    @Before
    public void setUp() throws Exception {
        coreCommissionService = spy(new CoreCommissionServiceImpl(coreCommissionRepository));
    }

    @Test
    public void findCommissionByTypeAndRole_ok() {
        doReturn(CoreCommissionDto.builder()
                .operationType(OperationType.INPUT)
                .userRole(UserRole.USER)
                .build())
                .when(coreCommissionRepository)
                .getCommission(any(OperationType.class), any(UserRole.class));

        CoreCommissionDto commission = coreCommissionService.findCommissionByTypeAndRole(OperationType.INPUT, UserRole.USER);

        assertNotNull(commission);
        assertEquals(OperationType.INPUT, commission.getOperationType());
        assertEquals(UserRole.USER, commission.getUserRole());

        verify(coreCommissionRepository, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
    }

    @Test(expected = CommissionsNotFoundException.class)
    public void findCommissionByTypeAndRole_not_found() {
        doThrow(CommissionsNotFoundException.class)
                .when(coreCommissionRepository)
                .getCommission(any(OperationType.class), any(UserRole.class));

        coreCommissionService.findCommissionByTypeAndRole(OperationType.INPUT, UserRole.USER);
    }
}