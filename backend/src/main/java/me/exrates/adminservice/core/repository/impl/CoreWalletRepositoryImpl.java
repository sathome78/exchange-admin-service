package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.domain.CoreTransactionDto;
import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.CoreWalletOperationDto;
import me.exrates.adminservice.core.domain.enums.ActionType;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.WalletTransferStatus;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.utils.BigDecimalProcessingUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Repository
public class CoreWalletRepositoryImpl implements CoreWalletRepository {

    private final CoreTransactionRepository coreTransactionRepository;
    private final NamedParameterJdbcOperations coreTemplate;

    @Autowired
    public CoreWalletRepositoryImpl(CoreTransactionRepository coreTransactionRepository,
                                    @Qualifier("coreNPTemplate") NamedParameterJdbcOperations coreTemplate) {
        this.coreTransactionRepository = coreTransactionRepository;
        this.coreTemplate = coreTemplate;
    }

    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        final String sql = "SELECT cur.id AS currency_id, " +
                "cur.name AS currency_name, " +
                "ur.id AS role_id, " +
                "ur.name AS role_name, " +
                "w.active_balance, " +
                "w.reserved_balance" +
                " FROM WALLET w" +
                " JOIN CURRENCY cur ON cur.id = w.currency_id AND cur.hidden = 0" +
                " JOIN USER u ON u.id = w.user_id" +
                " JOIN USER_ROLE ur ON ur.id = u.roleid" +
                " GROUP BY cur.id, ur.id" +
                " ORDER BY cur.id, ur.id";

        return coreTemplate.query(sql, (rs, row) -> InternalWalletBalancesDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .roleId(rs.getInt("role_id"))
                .roleName(UserRole.valueOf(rs.getString("role_name")))
                .totalBalance(rs.getBigDecimal("active_balance").add(rs.getBigDecimal("reserved_balance")))
                .build());
    }

    @Override
    public CoreWalletDto findByUserAndCurrency(int userId, int currencyId) {
        final String sql = "SELECT " +
                "w.id AS wallet_id, " +
                "w.currency_id, " +
                "w.user_id, " +
                "w.active_balance, " +
                "w.reserved_balance, " +
                "cur.name AS currency_name " +
                "FROM WALLET w " +
                "INNER JOIN CURRENCY cur ON cur.id = w.currency_id " +
                "WHERE w.user_id = :user_id AND w.currency_id = :currency_id";

        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("user_id", userId);
                put("currency_id", currencyId);
            }
        };
        try {
            return coreTemplate.queryForObject(sql, params, getWalletDtoRowMapper());
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public boolean isUserAllowedToManuallyChangeWalletBalance(Integer adminId, Integer userId) {
        final String sql = "SELECT uaara.user_id " +
                "FROM USER_ADMIN_AUTHORITY_ROLE_APPLICATION uaara " +
                "WHERE uaara.admin_authority_id = 8 AND uaara.user_id = :admin_id" +
                " AND uaara.applied_to_role_id = (SELECT u.roleid FROM USER u where u.id = :user_id) ";

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("admin_id", adminId);
            put("user_id", userId);
        }};
        return coreTemplate.queryForList(sql, params, Integer.class).size() > 0;
    }

    @Override
    public WalletTransferStatus walletBalanceChange(CoreWalletOperationDto walletOperation) {
        BigDecimal amount = walletOperation.getAmount();

        if (walletOperation.getOperationType() == OperationType.OUTPUT) {
            amount = amount.negate();
        }

        CoreCompanyWalletDto companyWallet = new CoreCompanyWalletDto();
        String sql = "SELECT " +
                "w.id AS wallet_id, " +
                "w.currency_id, " +
                "w.active_balance, " +
                "w.reserved_balance, " +
                "cw.id AS company_wallet_id, " +
                "cw.currency_id, " +
                "cw.balance, " +
                "cw.commission_balance " +
                "FROM WALLET w " +
                "JOIN COMPANY_WALLET cw ON cw.currency_id = w.currency_id " +
                "WHERE w.id = :wallet_id " +
                "FOR UPDATE"; //FOR UPDATE Important!

        Map<String, Object> params = new HashMap<>();
        params.put("wallet_id", String.valueOf(walletOperation.getWalletId()));

        CoreWalletDto wallet;
        try {
            wallet = coreTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                CoreWalletDto result = new CoreWalletDto();
                result.setId(rs.getInt("wallet_id"));
                result.setCurrencyId(rs.getInt("currency_id"));
                result.setActiveBalance(rs.getBigDecimal("active_balance"));
                result.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                /**/
                companyWallet.setId(rs.getInt("company_wallet_id"));
                CoreCurrencyDto currency = new CoreCurrencyDto();
                currency.setId(rs.getInt("currency_id"));
                companyWallet.setCurrency(currency);
                companyWallet.setBalance(rs.getBigDecimal("balance"));
                companyWallet.setCommissionBalance(rs.getBigDecimal("commission_balance"));
                return result;
            });
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            return WalletTransferStatus.WALLET_NOT_FOUND;
        }

        BigDecimal newActiveBalance;
        BigDecimal newReservedBalance;
        if (walletOperation.getBalanceType() == CoreWalletOperationDto.BalanceType.ACTIVE) {
            newActiveBalance = BigDecimalProcessingUtil.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
            newReservedBalance = wallet.getReservedBalance();
        } else {
            newActiveBalance = wallet.getActiveBalance();
            newReservedBalance = BigDecimalProcessingUtil.doAction(wallet.getReservedBalance(), amount, ActionType.ADD);
        }
        if (newActiveBalance.compareTo(BigDecimal.ZERO) < 0 || newReservedBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error(String.format("Negative balance: active %s, reserved %s ",
                    BigDecimalProcessingUtil.formatNonePoint(newActiveBalance, false),
                    BigDecimalProcessingUtil.formatNonePoint(newReservedBalance, false)));
            return WalletTransferStatus.CAUSED_NEGATIVE_BALANCE;
        }

        sql = "UPDATE WALLET w SET w.active_balance = :active_balance, w.reserved_balance = :reserved_balance WHERE w.id =:wallet_id";

        params = new HashMap<String, Object>() {
            {
                put("active_balance", newActiveBalance);
                put("reserved_balance", newReservedBalance);
                put("wallet_id", String.valueOf(walletOperation.getWalletId()));
            }
        };

        if (coreTemplate.update(sql, params) <= 0) {
            return WalletTransferStatus.WALLET_UPDATE_ERROR;
        }
        /**/
        if (walletOperation.getTransaction() == null) {
            CoreTransactionDto transaction = new CoreTransactionDto();
            transaction.setOperationType(walletOperation.getOperationType());
            transaction.setUserWallet(wallet);
            transaction.setCompanyWallet(companyWallet);
            transaction.setAmount(walletOperation.getAmount());
            transaction.setCommissionAmount(walletOperation.getCommissionAmount());
            transaction.setCommission(walletOperation.getCommission());
            transaction.setCurrency(companyWallet.getCurrency());
            transaction.setProvided(true);
            transaction.setActiveBalanceBefore(wallet.getActiveBalance());
            transaction.setReservedBalanceBefore(wallet.getReservedBalance());
            transaction.setCompanyBalanceBefore(companyWallet.getBalance());
            transaction.setCompanyCommissionBalanceBefore(companyWallet.getCommissionBalance());
            transaction.setSourceType(walletOperation.getSourceType());
            transaction.setSourceId(walletOperation.getSourceId());
            transaction.setDescription(walletOperation.getDescription());
            try {
                coreTransactionRepository.create(transaction);
            } catch (Exception ex) {
                log.error(ExceptionUtils.getStackTrace(ex));
                return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
            }
            walletOperation.setTransaction(transaction);
        } else {
            CoreTransactionDto transaction = walletOperation.getTransaction();
            transaction.setProvided(true);
            transaction.setUserWallet(wallet);
            transaction.setCompanyWallet(companyWallet);
            transaction.setActiveBalanceBefore(wallet.getActiveBalance());
            transaction.setReservedBalanceBefore(wallet.getReservedBalance());
            transaction.setCompanyBalanceBefore(companyWallet.getBalance());
            transaction.setCompanyCommissionBalanceBefore(companyWallet.getCommissionBalance());
            transaction.setSourceType(walletOperation.getSourceType());
            transaction.setSourceId(walletOperation.getSourceId());
            try {
                coreTransactionRepository.updateForProvided(transaction);
            } catch (Exception ex) {
                log.error(ExceptionUtils.getStackTrace(ex));
                return WalletTransferStatus.TRANSACTION_UPDATE_ERROR;
            }
            walletOperation.setTransaction(transaction);
        }
        return WalletTransferStatus.SUCCESS;
    }

    private RowMapper<CoreWalletDto> getWalletDtoRowMapper() {
        return (rs, i) -> CoreWalletDto.builder()
                .id(rs.getInt("wallet_id"))
                .currencyName(rs.getString("currency_name"))
                .userId(rs.getInt("user_id"))
                .currencyId(rs.getInt("currency_id"))
                .activeBalance(rs.getBigDecimal("active_balance"))
                .reservedBalance(rs.getBigDecimal("reserved_balance"))
                .build();
    }
}