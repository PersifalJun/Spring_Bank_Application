package bankApplication.service;

import bankApplication.model.Account;;
import bankApplication.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Value("${account.transfer-commission}")
    private BigDecimal commission;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void createAccount(Long userId) {
        List<Account> accounts = getAccountList(userId);
        Long accountId = accounts.stream()
                .filter(Objects::nonNull).mapToLong(Account::getId).max().orElse(0L) + 1L;
        accountRepository.save(userId, new Account(accountId, userId, BigDecimal.ZERO));
        System.out.println("Аккаунт создан!");
    }



    public void closeAccount(Long accountId) {

        Account accountToDelete = neededAccount(accountId,"Не найден нужный аккаунт для удаления!");
        List<Account> accounts = null;
        Long userId = null;
        for(Map.Entry<Long,List<Account>> entry : accountRepository.getUserAccountsMap().entrySet()){
            if(entry.getValue().contains(accountToDelete)){
                accounts = entry.getValue();
                userId = entry.getKey();
            }
        }
        List<Account> finalAccounts = accounts;
        Account firstAccount = neededAccount(finalAccounts.get(1).getId(),"Нет первого счёта у клиента");

        firstAccount.setMoneyAmount(firstAccount.getMoneyAmount().add(accountToDelete.getMoneyAmount()));
        try {
            accountRepository.deleteById(userId, accountId);
            System.out.println("Аккаунт : " + accountRepository.findByUserIdAndAccountId(userId, accountId).orElseThrow(
                    () -> new NoSuchElementException("Ничего не найдено!")) +
                    " удалён у пользователя c id: " + userId);
        } catch (NullPointerException ex) {
            ex.getMessage();
        }
    }


    public void makeDeposit(Long accountId, BigDecimal sum) {

        Account accountToMakeDeposit = neededAccount(accountId,
                "Не найден счет для внесения депозита");

        accountToMakeDeposit.setMoneyAmount(accountToMakeDeposit.getMoneyAmount().add(sum));
        System.out.println("Текущий счет: " + accountToMakeDeposit.getMoneyAmount() + "у аккаунта с Id: " + accountId);


    }

    public void transfer(Long accountIdSender, Long accountIdRecipient, BigDecimal sum) {

        Account senderAccount = neededAccount(accountIdSender, "Не найден счет отправителя");
        Account recipientAccount = neededAccount(accountIdRecipient, "Не найден счет получателя");

        recipientAccount.setMoneyAmount(recipientAccount.getMoneyAmount().add(sum.subtract(commission)));
        System.out.println("Текущее кол-во средств для аккаунта получателя: " + recipientAccount.getMoneyAmount());
        senderAccount.setMoneyAmount(senderAccount.getMoneyAmount().subtract(sum));
        System.out.println("Текущее кол-во средств для аккаунта отправителя: " + senderAccount.getMoneyAmount());
    }

    public void withdraw(Long accountId, BigDecimal sum) {

        Account accountToWithdraw = neededAccount(accountId, "Не найден счет для снятия денег");
        accountToWithdraw.setMoneyAmount(accountToWithdraw.getMoneyAmount().subtract(sum));
        System.out.println("Текущее кол-во средств после снятия: " + accountToWithdraw.getMoneyAmount());
    }

    private List<Account> getAccountList(Long userId) {
        return accountRepository.getUserAccountsMap().
                computeIfAbsent(userId, id -> new ArrayList<>());
    }


    private Account neededAccount(Long accountId, String exceptionMessage) {
        return accountRepository.getUserAccountsMap().values().stream().
                filter(Objects::nonNull).
                flatMap(Collection::stream).
                filter(account -> Objects.equals(account.getId(), accountId)).findFirst().
                orElseThrow(() -> new NoSuchElementException(exceptionMessage));
    }

}
