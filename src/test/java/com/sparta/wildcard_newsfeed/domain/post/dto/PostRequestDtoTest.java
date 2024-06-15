package com.sparta.wildcard_newsfeed.domain.post.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostRequestDtoTest {

    final String title = "제목";
    final String content = "내용";

    PostRequestDto dto;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        dto = new PostRequestDto();
        dto.setTitle(title);
        dto.setContent(content);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createPostRequestDto() {
        // given
        // when
        Set<ConstraintViolation<PostRequestDto>> validate = validator.validate(dto);

        // then
        assertThat(dto.getTitle()).isEqualTo(title);
        assertThat(dto.getContent()).isEqualTo(content);
        assertThat(dto.getFiles()).isEmpty();
        assertThat(validate).isEmpty();
    }

    @Test
    public void blankTitleAndContent() {
        // given
        PostRequestDto blankDto = new PostRequestDto();
        blankDto.setTitle("");
        blankDto.setContent("");

        // when
        Set<ConstraintViolation<PostRequestDto>> validate = validator.validate(blankDto);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate).hasSize(2);
        assertThat(validate)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("title")
                        && violation.getMessage().equals("제목은 필수 입력 값입니다."));
        assertThat(validate)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("content")
                        && violation.getMessage().equals("내용은 필수 입력 값입니다."));
    }
}