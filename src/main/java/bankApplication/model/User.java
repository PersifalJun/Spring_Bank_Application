package bankApplication.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
public class User {
    @NotNull(message = "ID пользователя не может быть null!")
    private Long id;
    @NotBlank(message = "Login пользователя не может быть пустым")
    private String login;
    @NotNull(message = "Аккаунты пользователя не могут быть null")
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
