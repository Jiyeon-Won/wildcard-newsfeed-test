package com.sparta.wildcard_newsfeed.domain.user.entity;

import com.sparta.wildcard_newsfeed.domain.user.dto.UserRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserEntityTest {

    final String usercode = "testId1234";
    final String password = "currentPWD999!";
    final String email = "test@gmail.com";
    final String name = "홍길동";
    final String introduce = "Hello World";
    final String changePassword = "changePWD123@@";

    private User user;
    @Mock
    private UserRequestDto dto;

    @BeforeEach
    void getUser() {
        user = User.builder()
                .usercode(usercode)
                .password(password)
                .name(name)
                .email(email)
                .introduce(introduce)
                .userStatus(UserStatusEnum.UNAUTHORIZED)
                .authUserAt(LocalDateTime.now())
                .userRoleEnum(UserRoleEnum.USER)
                .build();
    }

    @Test
    public void setUserStatus() {
        // given
        LocalDateTime localDateTime;
        UserStatusEnum newStatus = UserStatusEnum.DISABLED;
        localDateTime = user.getAuthUserAt();

        // when
        user.setUserStatus(newStatus);

        // then
        assertThat(user.getUserStatus()).isEqualTo(newStatus);
        assertTrue(localDateTime.isBefore(LocalDateTime.now()));
    }

    @Test
    public void update() {
        // given
        when(dto.getName()).thenReturn("원지연");
        when(dto.getEmail()).thenReturn("zzzz@naver.com");
        when(dto.getIntroduce()).thenReturn("change intro");
        when(dto.getChangePassword()).thenReturn(changePassword);

        // when
        user.update(dto);

        // then
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getIntroduce()).isEqualTo(dto.getIntroduce());
        assertThat(user.getPassword()).isEqualTo(dto.getChangePassword());
    }

    @Test
    public void updateUserStatus() {
        // given
        UserStatusEnum newStatus = UserStatusEnum.ENABLED;
        LocalDateTime localDateTime = user.getAuthUserAt();

        // when
        user.updateUserStatus();

        // then
        assertThat(user.getUserStatus()).isEqualTo(newStatus);
        assertThat(localDateTime).isBeforeOrEqualTo(LocalDateTime.now());
    }
}