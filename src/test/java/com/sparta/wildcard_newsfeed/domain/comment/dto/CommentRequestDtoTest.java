package com.sparta.wildcard_newsfeed.domain.comment.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCommentRequestDtoConstructor() {
        // given
        String content = "댓글";
        CommentRequestDto dto = new CommentRequestDto(content);

        // when
        // then
        assertThat(dto.getContent()).isEqualTo(content);
    }

    @Test
    public void testCommentContentNotBlank() {
        // given
        CommentRequestDto dto = new CommentRequestDto("");

        // when
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertEquals("댓글 내용은 필수 입력 값입니다.", violations.iterator().next().getMessage());
    }

    @Test
    public void testValidCommentContent() {
        // given
        CommentRequestDto dto = new CommentRequestDto("댓글");

        // when
        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
    }
}