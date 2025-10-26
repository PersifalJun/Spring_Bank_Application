package bankApplication.repository;

import bankApplication.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Validated
@Repository
public class UserRepository {
    private final List<User> users;

    public UserRepository() {
        this.users = new ArrayList<>();
    }

    public void save(User user) {
        if (users.stream().filter(Objects::nonNull).noneMatch(x -> Objects.equals(x.getId(), user.getId()))) {
            users.add(user);
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public boolean existsById(Long id) {
        return users.stream().anyMatch(u -> Objects.equals(u.getId(), id));
    }
}
