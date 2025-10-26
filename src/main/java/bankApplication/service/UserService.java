package bankApplication.service;


import bankApplication.exceptions.RegistryException;
import bankApplication.model.User;
import bankApplication.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Objects;

@Validated
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void showAllUsers() {
        userRepository.getUsers().forEach(System.out::println);
    }

    public void createUser(@NotBlank String login) {

        if (userRepository.getUsers().isEmpty()) {
            User firstUser = new User(1L, login, new ArrayList<>());
            userRepository.save(firstUser);
            System.out.println("Пользователь " + firstUser + " добавлен");
            return;
        }
        if (isRegistered(login)) {
            throw new RegistryException("Попробуйте ввести другой логин!");
        } else {
            Long id = userRepository.getUsers().stream().
                    filter(Objects::nonNull).
                    mapToLong(User::getId).
                    max().orElse(0L);
            User user = new User(id + 1L, login, new ArrayList<>());
            userRepository.save(user);
            System.out.println("Пользователь " + user + " добавлен");
        }
    }

    private boolean isRegistered(@NotBlank String login) {
        if (userRepository.getUsers().stream().
                filter(Objects::nonNull).anyMatch(user -> login.equals(user.getLogin()))) {
            System.out.println("Пользователь с таким логином уже зарегистрирован!");
            return true;
        }
        return false;
    }
}
