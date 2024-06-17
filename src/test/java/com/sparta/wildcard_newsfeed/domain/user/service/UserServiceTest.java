package com.sparta.wildcard_newsfeed.domain.user.service;

import com.sparta.wildcard_newsfeed.domain.file.service.FileService;
import com.sparta.wildcard_newsfeed.domain.user.dto.EmailSendEvent;
import com.sparta.wildcard_newsfeed.domain.user.dto.UserResponseDto;
import com.sparta.wildcard_newsfeed.domain.user.dto.UserSignupRequestDto;
import com.sparta.wildcard_newsfeed.domain.user.dto.UserSignupResponseDto;
import com.sparta.wildcard_newsfeed.domain.user.entity.AuthCodeHistory;
import com.sparta.wildcard_newsfeed.domain.user.entity.User;
import com.sparta.wildcard_newsfeed.domain.user.repository.AuthCodeRepository;
import com.sparta.wildcard_newsfeed.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    User user;

    private final String usercode = "testId9999";
    private final String email = "test@test.com";
    private final String password = "currentPWD999!";
    private final String encodedPassword = "encodedPassword";

    @BeforeEach
    void setUp() {
        user = new User(usercode, password, email);
    }

    @Test
    public void signup() {
        // given
        UserSignupRequestDto requestDto = new UserSignupRequestDto(usercode, password, email);
        when(userRepository.findByUsercodeOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(eq(password))).thenReturn(encodedPassword);
        ReflectionTestUtils.setField(user, "password", encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        UserSignupResponseDto responseDto = userService.signup(requestDto);

        // then
        assertThat(usercode).isEqualTo(responseDto.getUsercode());
        assertThat(email).isEqualTo(responseDto.getEmail());
    }
}