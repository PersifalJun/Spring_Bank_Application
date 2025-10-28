package bankApplication;

import bankApplication.service.OperationsConsoleListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("bankApplication");
        OperationsConsoleListener listener = context.getBean(OperationsConsoleListener.class);
        listener.printCommands();
        listener.start();
    }
}
