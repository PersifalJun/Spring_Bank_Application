package bankApplication.repository;

import bankApplication.exceptions.RegistaryException;
import bankApplication.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class UserRepository {
    private final List<User> users;
    private AccountRepository accountRepository;

    public UserRepository(){
        this.users = new ArrayList<>();
        this.accountRepository = new AccountRepository();
    }

    public void createUser(String login) {
        if (users.isEmpty()) {
            users.add(new User(1L, login,new ArrayList<>()));
            return;
        }
        if (isRegistered(login)) {
            throw new RegistaryException("Попробуйте ввести другой логин!");
        } else {
            Long id = users.stream().
                    filter(Objects::nonNull).
                    mapToLong(User::getId).
                    max().orElse(0L);
            users.add(new User(id + 1L, login,new ArrayList<>()));
            System.out.println("Пользователь добавлен");
        }
    }

    private boolean isRegistered(String login) {
        if (!(users.stream().
                filter(Objects::nonNull).
                filter(user -> login.equals(user.getLogin())).
                collect(Collectors.toList()).isEmpty())) {

            System.out.println("Пользователь с таким логином уже зарегистрирован!");
            return true;
        }
        return false;
    }

    public List<User> getUserList() {
        return users;
    }

    public void addUsersInUserAccountsMap(){
        for(User user : users){
            accountRepository.getUserAccounts().put(user,user.getAccountList());
        }
    }

}
