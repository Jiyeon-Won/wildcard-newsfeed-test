package com.sparta.wildcard_newsfeed.domain.post.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class PostPageRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Validator 초기화
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validPostPageRequestDto() {
        // given
        PostPageRequestDto dto = new PostPageRequestDto();
        setField(dto, "page", 1);
        setField(dto, "size", 1);
        setField(dto, "sortBy", "CREATED");
        setField(dto, "firstDate", "2024-05-01");
        setField(dto, "lastDate", "2024-05-27");

        // when
        Set<ConstraintViolation<PostPageRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void nullValuesInPostPageRequestDto() {
        // given
        PostPageRequestDto dto = new PostPageRequestDto();
        setField(dto, "page", 0);
        setField(dto, "size", 10);
        setField(dto, "sortBy", null);

        // when
        Set<ConstraintViolation<PostPageRequestDto>> violations = validator.validate(dto);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("page")
                && violation.getMessage().equals("0이 아닌 양수만 가능합니다."));
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("sortBy")
                && violation.getMessage().equals("정렬 기준 필수 입력 값입니다."));
    }
}