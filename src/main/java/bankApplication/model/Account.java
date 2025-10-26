package bankApplication.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Account {
    @NotNull(message = "ID аккаунта не может быть null!")
    private Long id;
    @NotNull(message = "Аккаунт должен быть привязан к пользователю!")
    private Long userId;
    @DecimalMin(value = "0.00")
    private BigDecimal moneyAmount;

    public Account(Long id, Long userId,
                   BigDecimal moneyAmount) {
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
