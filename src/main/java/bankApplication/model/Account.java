package bankApplication.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Account {
    @NotNull(message = "ID аккаунта не может быть null!")
    private Long id;
    @NotBlank(message = "Аккаунт должен быть привязан к пользователю!")
    private Long userId;
    @Min(0)
    private BigDecimal moneyAmount;

    public Account(Long id, Long userId,
                   @Value("${account.default-amount}") BigDecimal moneyAmount) {
        this.id = id;
        this.userId = userId;
        this.moneyAmount = moneyAmount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", moneyAmount=" + moneyAmount +
                '}';
    }
}
