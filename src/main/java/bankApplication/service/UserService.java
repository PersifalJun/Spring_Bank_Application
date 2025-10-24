package bankApplication.service;


import bankApplication.exceptions.RegistaryException;
import bankApplication.model.User;
import bankApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;


@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void showAllUsers(){
        userRepository.getUsers().forEach(System.out::println);
    }

    public void createUser(String login) {

        if (userRepository.getUsers().isEmpty()) {
            userRepository.save(new User(1L, login,new ArrayList<>()));
            return;
        }
        if (isRegistered(login)) {
            throw new RegistaryException("Попробуйте ввести другой логин!");
        } else {
            Long id = userRepository.getUsers().stream().
                    filter(Objects::nonNull).
                    mapToLong(User::getId).
                    max().orElse(0L);
            userRepository.save(new User(id + 1L, login,new ArrayList<>()));
            System.out.println("Пользователь добавлен");
        }
    }

    private boolean isRegistered(String login) {
        if (userRepository.getUsers().stream().
                filter(Objects::nonNull).anyMatch(user -> login.equals(user.getLogin()))) {
            System.out.println("Пользователь с таким логином уже зарегистрирован!");
            return true;
        }
        return false;
    }


}
