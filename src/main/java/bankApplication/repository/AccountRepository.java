package bankApplication.repository;


import bankApplication.model.Account;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public class AccountRepository {
    private final Map<Long, List<Account>> userAccountsMap;

    public AccountRepository() {
        this.userAccountsMap = new HashMap<>();
    }

    public void save(Long userId,Account account){
        userAccountsMap.computeIfAbsent(userId,id->new ArrayList<>()).add(account);
    }
    public Optional<Account> findByUserIdAndAccountId(Long userId,Long accountId){
        return userAccountsMap.getOrDefault(userId,List.of()).stream().
                filter(account->Objects.equals(account.getId(),accountId)).findFirst();
    }
    public void deleteById(Long userId,Long accountId){
        userAccountsMap.getOrDefault(userId,List.of())
                .removeIf(account->Objects.equals(account.getId(),accountId));
    }
    public Map<Long, List<Account>> getUserAccountsMap(){
        return userAccountsMap;
    }

}
