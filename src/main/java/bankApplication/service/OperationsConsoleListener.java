package bankApplication.service;

import bankApplication.commands.Commands;
import bankApplication.exceptions.NotEnoughAccountsException;
import bankApplication.exceptions.NotEnoughMoneyException;
import bankApplication.exceptions.RegistryException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Service
public class OperationsConsoleListener {
    private final AccountService accountService;
    private final UserService userService;
    private Scanner scanner = new Scanner(System.in);

    @Autowired
    public OperationsConsoleListener(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @PostConstruct
    public void start() {
        Thread consoleThread = new Thread(this::runConsole, "console-loop");
        consoleThread.start();
    }

    private void printCommands() {
        System.out.println("Выберите действие:" + "\n-USER_CREATE" +
                "\n-SHOW_ALL_USERS" + "\n-ACCOUNT_CREATE" +
                "\n-ACCOUNT_CLOSE" + "\n-ACCOUNT_DEPOSIT" + "\n-ACCOUNT_TRANSFER" + "\n-ACCOUNT_WITHDRAW" +
                "\n-ZERO->Выход");
    }

    private void runConsole() {

        boolean running = true;
        while (running) {
            try {
                printCommands();
                String raw = scanner.nextLine().trim().toUpperCase();
                if ("ZERO".equals(raw)) {
                    System.out.println("Выход");
                    running = false;
                }
                Commands commands;
                try {
                    commands = Commands.valueOf(raw);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Неизвестная команда");
                    continue;
                }
                switch (commands) {
                    case USER_CREATE -> {
                        System.out.println("Создание пользователя");
                        System.out.println("Введите логин пользователя:");
                        String login = scanner.nextLine();
                        try {
                            userService.createUser(login);
                        } catch (ConstraintViolationException | RegistryException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    case SHOW_ALL_USERS -> {
                        System.out.println("Все пользователи:");
                        userService.showAllUsers();
                    }
                    case ACCOUNT_CREATE -> {
                        System.out.println("Создание аккаунта:");
                        System.out.println("Введите Id пользователя:");
                        try {
                            Long id = Long.parseLong(scanner.nextLine());
                            accountService.createAccount(id);
                        } catch (ConstraintViolationException | NullPointerException | NoSuchElementException ex) {
                            System.out.println(ex.getMessage());
                        } catch (NumberFormatException ex) {
                            System.out.println("Неправильный формат ввода id");
                        }
                    }
                    case ACCOUNT_CLOSE -> {
                        System.out.println("Введите Id аккаунта для закрытия:");
                        try {
                            Long id = Long.parseLong(scanner.nextLine());
                            accountService.closeAccount(id);
                        } catch (ConstraintViolationException |
                                 NotEnoughAccountsException | NullPointerException
                                 | NoSuchElementException ex) {
                            System.out.println(ex.getMessage());
                        } catch (NumberFormatException ex) {
                            System.out.println("Неправильный формат ввода id");
                        }
                    }
                    case ACCOUNT_DEPOSIT -> {
                        System.out.println("Введите Id аккаунта и сумму для внесения депозита:");
                        try {
                            Long id = Long.parseLong(scanner.nextLine());
                            BigDecimal sum = new BigDecimal(scanner.nextLine().trim());
                            accountService.makeDeposit(id, sum);
                        } catch (ConstraintViolationException | NoSuchElementException
                                 | NullPointerException ex) {
                            System.out.println(ex.getMessage());
                        } catch (NumberFormatException ex) {
                            System.out.println("Неправильный формат ввода id или суммы");
                        }
                    }
                    case ACCOUNT_TRANSFER -> {
                        System.out.println("Введите Id счёта отправителя,Id счёта получателя и сумму для перевода средств:");
                        try {
                            Long senderId = Long.parseLong(scanner.nextLine());
                            Long recipientId = Long.parseLong(scanner.nextLine());
                            BigDecimal sum = new BigDecimal(scanner.nextLine().trim());
                            accountService.transfer(senderId, recipientId, sum);
                        } catch (ConstraintViolationException |
                                 NotEnoughAccountsException | NullPointerException |
                                 NoSuchElementException | NotEnoughMoneyException ex) {
                            System.out.println(ex.getMessage());
                        } catch (NumberFormatException ex) {
                            System.out.println("Неправильный формат ввода id или суммы");
                        }
                    }
                    case ACCOUNT_WITHDRAW -> {
                        System.out.println("Введите Id счёта и сумму для снятия средств:");
                        try {
                            Long id = Long.parseLong(scanner.nextLine());
                            BigDecimal sum = new BigDecimal(scanner.nextLine().trim());
                            accountService.withdraw(id, sum);
                        } catch (ConstraintViolationException |
                                 NotEnoughAccountsException | NullPointerException |
                                 NoSuchElementException | NotEnoughMoneyException ex) {
                            System.out.println(ex.getMessage());
                        } catch (NumberFormatException ex) {
                            System.out.println("Неправильный формат ввода id или суммы");
                        }
                    }
                }
            } catch (NullPointerException ex) {
                ex.getMessage();
            }
        }
    }
}
