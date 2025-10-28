package bankApplication.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @NotNull(message = "Идентификатор пользователя не может быть null")
    private Long id;
    @NotBlank(message = "Логин пользователя не может быть пустым")
    @Min(1)
    @Max(20)
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
