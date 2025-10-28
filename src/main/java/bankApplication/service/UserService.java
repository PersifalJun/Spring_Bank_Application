package bankApplication.service;


import bankApplication.exceptions.RegistryException;
import bankApplication.model.Account;
import bankApplication.model.User;
import bankApplication.ref.AccountRefUser;
import bankApplication.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Validated
@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRefUser accountRefUser;
    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;

    @Autowired
    public UserService(UserRepository userRepository, AccountRefUser accountRefUser) {
        this.userRepository = userRepository;
        this.accountRefUser = accountRefUser;
    }

    public List<User> showAllUsers() {
        return userRepository.getUsers();
    }

    public User createUser(@NotBlank String login) {
        User user = null;
        if (!isRegistered(login)) {
            long id = userRepository.getUsers().stream().
                    filter(Objects::nonNull).
                    mapToLong(User::getId).
                    max().orElse(0L);
            List<Account> accounts = new ArrayList<>();
            Long maxId = accountRefUser.getMaxAccountId();
            accounts.add(new Account(maxId, id + 1L, defaultAmount));
            user = new User(id + 1L, login, accounts);
            userRepository.save(user);
        }
        return user;
    }

    private boolean isRegistered(@NotBlank String login) {
        if (userRepository.getUsers().stream().
                filter(Objects::nonNull).anyMatch(user -> login.equals(user.getLogin()))) {
            throw new RegistryException("Пользователь с таким логином уже зарегистрирован!");
        }
        return false;
    }
}
