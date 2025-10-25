package bankApplication.service;

import bankApplication.commands.Commands;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private void runConsole() {
        System.out.println("Выберите действие:" + "\n1.USER_CREATE" +
                "\n2.SHOW_ALL_USERS" + "\n3.ACCOUNT_CREATE" +
                "\n4.ACCOUNT_CLOSE" + "\n5.ACCOUNT_DEPOSIT" + "\n6.ACCOUNT_TRANSFER" + "\n7.ACCOUNT_WITHDRAW" +
                "\nZERO->Выход");

        boolean running = true;

        while (running) {

            try {
                String raw = scanner.nextLine().trim().toUpperCase();
                if ("ZERO".equals(raw)) {
                    running = false;
                    return;
                }
                Commands commands;
                try {
                    commands = Commands.valueOf(raw);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Неизвестная команда");
                    return;
                }
                switch (commands) {
                    case USER_CREATE -> {
                        System.out.println("Создание пользователя");
                        System.out.println("Введите логин пользователя:");
                        String login = scanner.nextLine();
                        userService.createUser(login);
                    }
                    case SHOW_ALL_USERS -> {
                        System.out.println("Все пользователи:");
                        userService.showAllUsers();
                    }
                    case ACCOUNT_CREATE -> {
                        System.out.println("Создание аккаунта:");
                        System.out.println("Введите Id пользователя:");
                        Long id = null;
                        try {
                            id = Long.parseLong(scanner.nextLine());
                        } catch (NullPointerException ex) {
                            ex.getMessage();
                        }
                        accountService.createAccount(id);
                    }
                    case ACCOUNT_CLOSE -> {
                        System.out.println("Введите Id аккаунта для закрытия:");
                        Long id = null;
                        try {
                            id = Long.parseLong(scanner.nextLine());
                        } catch (NullPointerException ex) {
                            ex.getMessage();
                        }
                        accountService.closeAccount(id);
                    }
                    case ACCOUNT_DEPOSIT -> {
                        System.out.println("Введите Id аккаунта и сумму для внесения депозита:");
                        Long id = null;
                        BigDecimal sum = null;
                        try {
                            id = Long.parseLong(scanner.nextLine());
                            sum = scanner.nextBigDecimal();
                        } catch (NullPointerException ex) {
                            ex.getMessage();
                        }
                        accountService.makeDeposit(id, sum);
                    }
                    case ACCOUNT_TRANSFER -> {
                        System.out.println("Введите Id счёта отправителя,Id счёта получателя и сумму для перевода средств:");
                        Long senderId = null;
                        Long recepientId = null;
                        BigDecimal sum = null;
                        try {
                            senderId = Long.parseLong(scanner.nextLine());
                            recepientId = Long.parseLong(scanner.nextLine());
                            sum = scanner.nextBigDecimal();
                        } catch (NullPointerException ex) {
                            ex.getMessage();
                        }
                        if(sum==null){
                            throw new NullPointerException("Некорректная сумма!");
                        }
                        accountService.transfer(senderId, recepientId, sum);
                    }
                    case ACCOUNT_WITHDRAW -> {
                        System.out.println("Введите Id счёта и сумму для снятия средств:");
                        Long id = null;
                        BigDecimal sum = null;
                        try {
                            id = Long.parseLong(scanner.nextLine());
                            sum = scanner.nextBigDecimal();
                        } catch (NullPointerException ex) {
                            ex.getMessage();
                        }
                        accountService.withdraw(id, sum);
                    }
                    case ZERO -> {
                        System.out.println("Выход");
                        running = false;
                    }
                }
            } catch (NullPointerException ex) {
                ex.getMessage();
            }
        }
    }
}
