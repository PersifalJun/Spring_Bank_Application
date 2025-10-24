package bankApplication.repository;

import bankApplication.exceptions.RegistaryException;
import bankApplication.model.Account;
import bankApplication.model.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class AccountRepository {
    private final Map<User, List<Account>> userAccounts;
    private BigDecimal moneyAmount;

    public AccountRepository() {
        this.userAccounts = new HashMap<>();
    }

    public void createAccount(Long userId) {
        for (Map.Entry<User, List<Account>> entry : userAccounts.entrySet()) {
            if (!entry.getKey().getId().equals(userId)) {
                throw new RegistaryException("Нельзя создать аккаунт для незарегистрированного пользователя!");
            } else {
                if (entry.getKey().getAccountList().isEmpty()) {
                    entry.getValue().add(new Account(1L, userId, moneyAmount));
                } else {
                    Long accountId = userAccounts.
                            values().stream().
                            filter(Objects::nonNull).
                            flatMap(Collection::stream).
                            mapToLong(Account::getId).max().orElse(0L);
                    entry.getValue().add(new Account(accountId + 1L, userId, moneyAmount));
                }
            }
        }
    }

    public void closeAccount(Long accountId) {
        for (Map.Entry<User, List<Account>> entry : userAccounts.entrySet()) {
            if ((!entry.getValue().isEmpty()) || !(accountId.equals(entry.getValue().
                    stream().
                    filter(Objects::nonNull).
                    map(Account::getId)))) {
                throw new NoSuchElementException("Такого аккаунта не существует!");
            } else {
                Account firstAccount = userAccounts.values().
                        stream().
                        filter(Objects::nonNull).
                        flatMap(Collection::stream).
                        filter(account -> account.getId().equals(1L)).
                        findFirst().orElseThrow(() -> new NoSuchElementException("Не удалось найти первый аккаунт"));

                Account closedAccount = userAccounts.values().
                        stream().
                        filter(Objects::nonNull).
                        flatMap(Collection::stream).
                        filter(account -> accountId.equals(account.getId())).
                        findFirst().orElseThrow(() -> new NoSuchElementException("Не удалось найти аккаунт для закрытия"));

                firstAccount.getMoneyAmount().add(closedAccount.getMoneyAmount());
                try {
                    userAccounts.values().remove(closedAccount);
                    System.out.println("Аккаунт с id " + accountId + " был удален!");
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void makeDeposit(Long accountId, BigDecimal sum){


    }

    public Map<User, List<Account>> getUserAccounts() {
        return userAccounts;
    }

}
