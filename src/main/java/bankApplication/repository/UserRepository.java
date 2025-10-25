package bankApplication.repository;

import bankApplication.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Repository
public class UserRepository {
    private final List<User> users;

    public UserRepository() {
        this.users = new ArrayList<>();
    }

    public void save(User user) {
        if (users.stream().noneMatch(x -> Objects.equals(x.getId(), user.getId()))) {
            users.add(user);
            System.out.println("Аккаунт создан!");
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
