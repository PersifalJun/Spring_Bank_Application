package bankApplication.service;

import bankApplication.exceptions.NotEnoughAccountsException;
import bankApplication.exceptions.NotEnoughMoneyException;
import bankApplication.model.Account;
import bankApplication.model.User;
import bankApplication.repository.AccountRepository;
import bankApplication.repository.UserRepository;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;

@Validated
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Value("${account.transfer-commission}")
    private BigDecimal commission;
    @Value("${account.default-amount}")
    private BigDecimal moneyAmount;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public void createAccount(@NotNull Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Невозможно создать аккаунт для несуществующего пользователя");
        }
        Long accountId = accountRepository.getUserAccountsMap().values().stream().
                filter(Objects::nonNull).
                flatMap(Collection::stream).
                map(Account::getId).
                filter(Objects::nonNull).
                mapToLong(Long::longValue).
                max().orElse(0L) + 1L;
        accountRepository.save(userId, new Account(accountId, userId, moneyAmount));
        addNewAccountToUserList(userId, accountId);
        System.out.println("Аккаунт с id: " + accountId + " создан для пользователя с id " + userId);
    }

    public void closeAccount(@NotNull Long accountId) {
        Account accountToDelete = accountRepository.findById(accountId, "Не найден нужный аккаунт для удаления!");
        List<Account> accounts = null;
        Long userId = null;
        for (Map.Entry<Long, List<Account>> entry : accountRepository.getUserAccountsMap().entrySet()) {
            if (entry.getValue().contains(accountToDelete)) {
                accounts = entry.getValue();
                userId = entry.getKey();
            }
        }
        @NotNull List<Account> finalAccounts = accounts;
        if (!Objects.equals(finalAccounts.get(0).getId(), accountToDelete.getId()) && finalAccounts.size() >= 2) {
            Account firstAccount = accountRepository.findById(finalAccounts.get(0).getId(), "Нет первого счёта у клиента");
            firstAccount.setMoneyAmount(firstAccount.getMoneyAmount().add(accountToDelete.getMoneyAmount()));
            try {
                accountRepository.deleteById(userId, accountId);
                deleteAccountFromUserList(userId, accountToDelete);
                System.out.println("Аккаунт c Id : " + accountId + " закрыт у пользователя c id: " + userId);
            } catch (NullPointerException ex) {
                ex.getMessage();
            }
        } else {
            throw new NotEnoughAccountsException("Неправильное закрытие счета!");
        }
    }

    public void makeDeposit(@NotNull Long accountId,
                            @DecimalMin(value = "10.00") BigDecimal sum) {
        Account accountToMakeDeposit = accountRepository.findById(accountId,
                "Не найден счет для внесения депозита");

        accountToMakeDeposit.setMoneyAmount(accountToMakeDeposit.getMoneyAmount().add(sum));
        System.out.println("Текущий счет: " + accountToMakeDeposit.getMoneyAmount() + " у аккаунта с Id: " + accountId);
    }

    public void transfer(@NotNull Long accountIdSender,
                         @NotNull Long accountIdRecipient,
                         @DecimalMin(value = "10.00") BigDecimal sum) {

        Account senderAccount = accountRepository.findById(accountIdSender, "Не найден счет отправителя");
        Account recipientAccount = accountRepository.findById(accountIdRecipient, "Не найден счет получателя");
        if (senderAccount.getMoneyAmount().compareTo(sum) >= 0) {
            recipientAccount.setMoneyAmount(recipientAccount.getMoneyAmount().add(sum.subtract(commission)));
            System.out.println("Текущее кол-во средств для аккаунта получателя c учетом комиссии: " + recipientAccount.getMoneyAmount());
            senderAccount.setMoneyAmount(senderAccount.getMoneyAmount().subtract(sum));
            System.out.println("Текущее кол-во средств для аккаунта отправителя: " + senderAccount.getMoneyAmount());
        } else {
            throw new NotEnoughMoneyException("Недостаточно средств для перевода!");
        }
    }

    public void withdraw(@NotNull Long accountId, @DecimalMin(value = "10.00") BigDecimal sum) {

        Account accountToWithdraw = accountRepository.findById(accountId, "Не найден счет для снятия денег");
        if (accountToWithdraw.getMoneyAmount().compareTo(sum) >= 0) {
            accountToWithdraw.setMoneyAmount(accountToWithdraw.getMoneyAmount().subtract(sum));
            System.out.println("Текущее кол-во средств после снятия: " + accountToWithdraw.getMoneyAmount());
        } else {
            throw new NotEnoughMoneyException("Недостаточно средств для снятия средств!");
        }
    }

    private void addNewAccountToUserList(Long userId, Long accountId) {
        Account account = accountRepository.findById(accountId
                , "Аккаунт для добавления в пользовательский список аккаунтов не найден!");

        @NotNull Long finalUserId = userId;
        User userWhoCreateNewAccount = userRepository.getUsers().
                stream().
                filter(Objects::nonNull).filter(user -> Objects.equals(user.getId(), finalUserId)).
                findFirst().orElseThrow(() -> new NoSuchElementException("Не найден пользователь"));
        userWhoCreateNewAccount.getAccountList().add(account);
    }

    private void deleteAccountFromUserList(Long userId, Account account) {

        User userWhoCloseAccount = userRepository.getUsers().
                stream().
                filter(Objects::nonNull).filter(user -> Objects.equals(user.getId(), userId)).
                findFirst().orElseThrow(() -> new NoSuchElementException("Не найден пользователь"));
        userWhoCloseAccount.getAccountList().remove(account);
    }
}
