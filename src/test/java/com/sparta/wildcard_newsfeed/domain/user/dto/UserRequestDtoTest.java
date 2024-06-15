package com.sparta.wildcard_newsfeed.domain.user.dto;

import com.sparta.wildcard_newsfeed.exception.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;


import static com.sparta.wildcard_newsfeed.exception.validation.ValidationGroups.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRequestDtoTest {

    final String email = "test@gmail.com";
    final String introduce = "Hello World";
    final String password = "currentPWD999!";
    final String changePassword = "changePWD123@@";

    @Mock
    UserRequestDto dto;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("")
    public void invalidEmailPattern() {
        // 유효하지 않은 이메일
        String[] invalidEmails = {
                "plainaddress",
                "@missingusername.com",
                "user.name@.missingdomain.com",
                "username@missingtld.",
                "username@invalid-.com",
                "username@.com",
                "user.name@domain..com",
                "user@domain,com",
                "user@domain..com",
                "user@-domain.com",
                "user@domain-.com",
                ".user@domain.com",
                "user@domain..com"
        };

        // 유효하지 않은 이메일 테스트
        for (String testEmail : invalidEmails) {
            // given
            ReflectionTestUtils.setField(dto, "email", testEmail);

            // when
            Set<ConstraintViolation<UserRequestDto>> validate = validator.validate(dto);

            // then
            assertThat(validate).describedAs("유효하지 않은 이메일 테스트 실패: " + testEmail).hasSize(1);
            assertThat(validate.iterator().next().getMessage()).isEqualTo("이메일 형식이 올바르지 않습니다.");
        }
    }

    @Test
    public void invalidPasswordPattern() {
        // given
        ReflectionTestUtils.setField(dto, "password", "invalidPasswordPattern");
        ReflectionTestUtils.setField(dto, "changePassword", "invalidPasswordPattern");

        // when
        Set<ConstraintViolation<UserRequestDto>> validate = validator.validate(dto);

        // then
        assertThat(validate).hasSize(1);
        assertThat(validate.iterator().next().getMessage()).isEqualTo("비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.");
    }

    @Test
    public void invalidPasswordLength() {
        // given - 짧을 때
        ReflectionTestUtils.setField(dto, "password", "shortPW@1");
        ReflectionTestUtils.setField(dto, "changePassword", "shortPW@1");

        // when - 짧을 때
        Set<ConstraintViolation<UserRequestDto>> shortValidate = validator.validate(dto);

        // then - 짧을 때
        assertThat(shortValidate).hasSize(2);
        assertThat(shortValidate.iterator().next().getMessage()).isEqualTo("비밀번호는 최소 10글자 이상이어야 합니다.");
    }
}