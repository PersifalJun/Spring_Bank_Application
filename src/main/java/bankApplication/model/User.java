package bankApplication.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Component
@AllArgsConstructor
public class User {
    @NotNull(message = "ID пользователя не может быть null!")
    private Long id;
    @NotNull(message = "Login пользователя не может быть пустым")
    private String login;
    private List<Account> accountList;



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", accountList=" + accountList +
                '}';
    }
}
