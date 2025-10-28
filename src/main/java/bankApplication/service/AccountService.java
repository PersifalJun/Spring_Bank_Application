package bankApplication.service;

import bankApplication.exceptions.*;
import bankApplication.model.Account;
import bankApplication.model.User;
import bankApplication.ref.AccountRefUser;
import bankApplication.repository.AccountRepository;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;

@Transactional
@Validated
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountRefUser accountRefUser;

    @Value("${account.transfer-commission}")
    private BigDecimal commission;
    @Value("${account.default-amount}")
    private BigDecimal moneyAmount;

    @Autowired
    public AccountService(AccountRepository accountRepository, AccountRefUser accountRefUser) {
        this.accountRepository = accountRepository;
        this.accountRefUser = accountRefUser;
    }

    public void createAccount(@NotNull Long userId) {
        if (!accountRepository.getUserAccountsMap().containsKey(userId)) {
            throw new NoUserException("Невозможно создать аккаунт для несуществующего пользователя");
        }
        Long accountId = accountRepository.getUserAccountsMap().values().stream().
                filter(Objects::nonNull).
                flatMap(Collection::stream).
                map(Account::getId).
                filter(Objects::nonNull).
                mapToLong(Long::longValue).
                max().orElse(0L) + 1L;
        accountRepository.save(userId, new Account(accountId, userId, moneyAmount));
    }

    public void closeAccount(@NotNull Long accountId) {
        Account accountToDelete = accountRepository.findById(accountId);
        List<Account> accounts = null;
        Long userId = null;
        for (Map.Entry<Long, List<Account>> entry : accountRepository.getUserAccountsMap().entrySet()) {
            if (entry.getValue().contains(accountToDelete)) {
                accounts = entry.getValue();
                userId = entry.getKey();
            }
        }
        @NotNull List<Account> finalAccounts = accounts;
        if (finalAccounts.size() == 1) {
            throw new NotEnoughAccountsException("У пользователя всего один счет. Он не может его закрыть");
        } else if (finalAccounts.isEmpty()) {
            throw new NotEnoughAccountsException("У пользователя нет счетов");
        } else if (finalAccounts.get(0).getId().equals(accountId)) {
            throw new FirstAccountClosedException("Нельзя закрыть первый аккаунт пользователя");
        } else {
            Account firstAccount = accountRepository.findById(finalAccounts.get(0).getId());
            firstAccount.setMoneyAmount(firstAccount.getMoneyAmount().add(accountToDelete.getMoneyAmount()));
            try {
                accountRepository.deleteById(userId, accountId);
            } catch (RuntimeException ex) {
                throw new NoAccountException("Не найден аккаунт для удаления");
            }
        }
    }

    public Account makeDeposit(@NotNull Long accountId,
                               @DecimalMin(value = "10.00") BigDecimal sum) {
        Account accountToMakeDeposit = accountRepository.findById(accountId);
        accountToMakeDeposit.setMoneyAmount(accountToMakeDeposit.getMoneyAmount().add(sum));
        return accountToMakeDeposit;
    }

    public Account transfer(@NotNull Long accountIdSender,
                            @NotNull Long accountIdRecipient,
                            @DecimalMin(value = "10.00") BigDecimal sum) {
        Account senderAccount;
        Account recipientAccount;

        try {
            senderAccount = accountRepository.findById(accountIdSender);
        } catch (RuntimeException ex) {
            throw new NoAccountException("Не найден аккаунт отправителя");
        }
        try {
            recipientAccount = accountRepository.findById(accountIdRecipient);
        } catch (RuntimeException ex) {
            throw new NoAccountException("Не найден аккаунт отправителя");
        }
        User sender = accountRefUser.identifyUserByAccount(senderAccount);
        User recipient = accountRefUser.identifyUserByAccount(recipientAccount);

        if (senderAccount.equals(recipientAccount)) {
            throw new IdenticalAccountException("Счета аккаунтов идентичны");
        } else if (senderAccount.getMoneyAmount().compareTo(sum) < 0) {
            throw new NotEnoughMoneyException("Недостаточно средств для перевода!");

        } else if (sender.getId().equals(recipient.getId())) {
            throw new SameSenderException("Нельзя осуществлять перевод средств между своими счетами!");
        } else {
            recipientAccount.setMoneyAmount(recipientAccount.getMoneyAmount().add(sum.subtract(commission)));
            senderAccount.setMoneyAmount(senderAccount.getMoneyAmount().subtract(sum));
        }
        return senderAccount;
    }

    public Account withdraw(@NotNull Long accountId, @DecimalMin(value = "10.00") BigDecimal sum) {
        Account accountToWithdraw = accountRepository.findById(accountId);
        if (accountToWithdraw.getMoneyAmount().compareTo(sum) < 0) {
            throw new NotEnoughMoneyException("Недостаточно средств для снятия средств!");
        } else {
            accountToWithdraw.setMoneyAmount(accountToWithdraw.getMoneyAmount().subtract(sum));
        }
        return accountToWithdraw;
    }
}
