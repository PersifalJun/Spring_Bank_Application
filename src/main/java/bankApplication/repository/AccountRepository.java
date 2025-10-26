package bankApplication.repository;

import bankApplication.model.Account;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Validated
@Repository
public class AccountRepository {
    private final Map<Long, List<Account>> userAccountsMap;

    public AccountRepository() {
        this.userAccountsMap = new HashMap<>();
    }

    public void save(@NotNull Long userId, Account account) {
        if (Objects.nonNull(account)) {
            userAccountsMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(account);
        }
    }

    public Account findById(@NotNull Long accountId, String exceptionMessage) {
        return userAccountsMap.values().stream().
                filter(Objects::nonNull).
                flatMap(Collection::stream).
                filter(account -> Objects.equals(account.getId(), accountId)).findFirst()
                .orElseThrow(() -> new NoSuchElementException(exceptionMessage));

    }

    public void deleteById(@NotNull Long userId, @NotNull Long accountId) {
        userAccountsMap.getOrDefault(userId, List.of())
                .removeIf(account -> Objects.equals(account.getId(), accountId));
    }

    public Map<Long, List<Account>> getUserAccountsMap() {
        return userAccountsMap;
    }

}
