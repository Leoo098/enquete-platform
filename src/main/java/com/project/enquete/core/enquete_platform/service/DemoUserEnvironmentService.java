package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.dto.request.OptionDTO;
import com.project.enquete.core.enquete_platform.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.model.TimeUnit;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DemoUserEnvironmentService {

    private final PollService pollService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public User createTemporaryUser(){
        cleanupOldDemoUsers();

        String randomUserId = UUID.randomUUID().toString().substring(0, 5);
        User user = new User();
        user.setUsername("user " + randomUserId);
        user.setEmail("demo_" + randomUserId + "@test.com");
        user.setPassword(encoder.encode(UUID.randomUUID().toString()));
        user.setDemoUser(true);

        userRepository.save(user);

        return user;
    }

    public void createSamplePoll() {
        OptionDTO option1 = new OptionDTO("Java", null);
        OptionDTO option2 = new OptionDTO("JavaScript", null);
        OptionDTO option3 = new OptionDTO("Python", null);

        PollDTO dto = new PollDTO(
                "Qual sua linguagem de programação favorita?",
                2,
                TimeUnit.HOURS,
                List.of(option1, option2, option3),
                "private"
        );

        pollService.createPoll(dto);
    }

    public void cleanupOldDemoUsers() {
        Instant deleteTime = Instant.now().minus(2, ChronoUnit.HOURS);
        userRepository.deleteByDemoUserTrueAndCreatedAtBefore(deleteTime);
    }
}
