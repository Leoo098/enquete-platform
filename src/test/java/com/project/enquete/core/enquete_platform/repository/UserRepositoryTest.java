package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void salvarTest(){
        User user = new User();
        user.setEmail("leo@gmail.com");
        user.setPassword("1234");

        var userSalvo = userRepository.save(user);
        System.out.println("Usuário salvo: " + userSalvo);
    }

    @Test
    public void atualizarDados(){

        User user = userRepository.findById(UUID.fromString("bfbfbc85-42ee-44a3-98da-e0c44642bf13")).orElse(null);

        //OU
//        var optionId = UUID.fromString("bfbfbc85-42ee-44a3-98da-e0c44642bf13");
//        Optional<User> possibleUser = userRepository.findById(optionId);

        if (user != null) {
            user.setEmail("leo010@gmail.com");
            userRepository.save(user);
        }
        else {
            System.out.println("Autor não encontrado!");
        }
    }

    @Test
    public void ListTest(){
        List<User> list = userRepository.findAll();
        list.forEach(System.out::println);
    }

}
