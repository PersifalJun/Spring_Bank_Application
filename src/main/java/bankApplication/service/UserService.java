package bankApplication.service;


import bankApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void showAllUsers(){
        userRepository.getUserList().forEach(System.out::println);
    }
    public void create(String login){
        userRepository.createUser(login);
    }


}
