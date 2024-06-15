package com.sparta.wildcard_newsfeed.domain.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.sparta.wildcard_newsfeed.exception.validation.ValidationGroups.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserSignupRequestDtoTest {

    final String usercode = "testId1234";
    final String password = "currentPWD999!";
    final String email = "test@gmail.com";

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validSignupRequestDto() {
        // given
        UserSignupRequestDto dto = UserSignupRequestDto.builder()
                .usercode(usercode)
                .password(password)
                .email(email)
                .build();

        // when
        Set<ConstraintViolation<UserSignupRequestDto>> validate = validator.validate(dto);

        // then
        assertTrue(validate.isEmpty());
    }

    @Test
    public void invalidUsercodePattern() {
        // given
        UserSignupRequestDto dto = UserSignupRequestDto.builder()
                .usercode("qwerasdf!@#$")
                .password(password)
                .email(email)
                .build();

        // when
        Set<ConstraintViolation<UserSignupRequestDto>> validate = validator.validate(dto, PatternGroup.class);

        // then
        assertThat(validate.size()).isEqualTo(1);
        assertThat(validate.iterator().next().getMessage()).isEqualTo("대소문자 포함 영문 + 숫자만 입력해 주세요");
    }

    @Test
    public void invalidUsercodeLength() {
        // given - 짧을 때
        UserSignupRequestDto shortDto = UserSignupRequestDto.builder()
                .usercode("short")
                .password(password)
                .email(email)
                .build();
        // given - 길 때
        UserSignupRequestDto longDto = UserSignupRequestDto.builder()
                .usercode("longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglong")
                .password(password)
                .email(email)
                .build();

        // when - 짧을 때
        Set<ConstraintViolation<UserSignupRequestDto>> shortValidate = validator.validate(shortDto, SizeGroup.class);
        // when - 길 때
        Set<ConstraintViolation<UserSignupRequestDto>> longValidate = validator.validate(longDto, SizeGroup.class);

        // then - 짧을 때
        assertThat(shortValidate.size()).isEqualTo(1);
        assertThat(shortValidate.iterator().next().getMessage()).isEqualTo("최소 10자 이상, 20자 이하로 입력해 주세요");
        // then - 길 때
        assertThat(longValidate.size()).isEqualTo(1);
        assertThat(longValidate.iterator().next().getMessage()).isEqualTo("최소 10자 이상, 20자 이하로 입력해 주세요");
    }

    @Test
    public void blankUsercode() {
        // given
        UserSignupRequestDto dto = UserSignupRequestDto.builder()
                .usercode("")
                .password(password)
                .email(email)
                .build();

        // when
        Set<ConstraintViolation<UserSignupRequestDto>> validate = validator.validate(dto, NotBlankGroup.class);

        // then
        assertThat(validate).hasSize(1);
        assertThat(validate.iterator().next().getMessage()).isEqualTo("아이디를 작성해주세요");
    }

    @Test
    public void invalidPasswordPattern() {
        // given
        UserSignupRequestDto dto = UserSignupRequestDto.builder()
                .usercode(usercode)
                .password("invalidPasswordPattern")
                .email(email)
                .build();

        // when
        Set<ConstraintViolation<UserSignupRequestDto>> validate = validator.validate(dto, PatternGroup.class);

        // then
        assertThat(validate).hasSize(1);
        assertThat(validate.iterator().next().getMessage()).isEqualTo("비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해 주세요");
    }

    @Test
    public void invalidPasswordLength() {
        // given - 짧을 때
        UserSignupRequestDto shortDto = UserSignupRequestDto.builder()
                .usercode(usercode)
                .password("short")
                .email(email)
                .build();

        // when - 짧을 때
        Set<ConstraintViolation<UserSignupRequestDto>> shortValidate = validator.validate(shortDto, SizeGroup.class);

        // then - 짧을 때
        assertThat(shortValidate).hasSize(1);
        assertThat(shortValidate.iterator().next().getMessage()).isEqualTo("최소 10자 이상 입력해 주세요");
    }

    @Test
    public void blankPassword() {
        // given
        UserSignupRequestDto dto = UserSignupRequestDto.builder()
                .usercode(usercode)
                .password("")
                .email(email)
                .build();

        // when
        Set<ConstraintViolation<UserSignupRequestDto>> validate = validator.validate(dto, NotBlankGroup.class);

        // then
        assertThat(validate).hasSize(1);
        assertThat(validate.iterator().next().getMessage()).isEqualTo("비밀번호를 작성해주세요");
    }

    @Test
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
                "user@.domain.com",
                "user@domain_com",
                "user@domain,com"
        };

        // 유효하지 않은 이메일 테스트
        for (String email : invalidEmails) {
            // given
            UserSignupRequestDto dto = UserSignupRequestDto.builder()
                    .usercode(usercode)
                    .password(password)
                    .email(email)
                    .build();

            // when
            Set<ConstraintViolation<UserSignupRequestDto>> validate = validator.validate(dto, PatternGroup.class);

            // then
            assertThat(validate).describedAs("유효하지 않은 이메일 테스트 실패: " + email).hasSize(1);
            assertThat(validate.iterator().next().getMessage()).isEqualTo("이메일 형식에 맞지 않습니다.");
        }
    }

    @Test
    public void invalidEmailLength() {
        // given
        UserSignupRequestDto dto = UserSignupRequestDto.builder()
                .usercode(usercode)
                .password(password)
                .email("a".repeat(246) + "@gmail.com") // 255 초과
                .build();

        // when
        Set<ConstraintViolation<UserSignupRequestDto>> validate = validator.validate(dto, SizeGroup.class);

        // then
        assertThat(validate).hasSize(1);
        assertThat(validate.iterator().next().getMessage()).isEqualTo("이메일 입력 범위를 초과하였습니다.");
    }

    @Test
    public void blankEmail() {
        // given
        UserSignupRequestDto dto = UserSignupRequestDto.builder()
                .usercode(usercode)
                .password(password)
                .email("")
                .build();

        // when
        Set<ConstraintViolation<UserSignupRequestDto>> validate = validator.validate(dto, NotBlankGroup.class);

        // then
        assertThat(validate).hasSize(1);
        assertThat(validate.iterator().next().getMessage()).isEqualTo("이메일을 입력해주세요.");
    }
}