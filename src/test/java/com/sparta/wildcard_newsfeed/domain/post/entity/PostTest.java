package com.sparta.wildcard_newsfeed.domain.post.entity;

import com.sparta.wildcard_newsfeed.domain.post.dto.PostRequestDto;
import com.sparta.wildcard_newsfeed.domain.user.entity.User;
import com.sparta.wildcard_newsfeed.domain.user.entity.UserRoleEnum;
import com.sparta.wildcard_newsfeed.domain.user.entity.UserStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PostTest {

    final String title = "제목";
    final String content = "내용";

    private User user;
    private PostRequestDto dto;
    private Post post;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .usercode("testId1234")
                .password("currentPWD999!")
                .name("홍길동")
                .email("test@gmail.com")
                .introduce("Hello World")
                .userStatus(UserStatusEnum.UNAUTHORIZED)
                .authUserAt(LocalDateTime.now())
                .userRoleEnum(UserRoleEnum.USER)
                .build();

        dto = new PostRequestDto();
        dto.setTitle(title);
        dto.setContent(content);

        post = new Post(dto, user);
    }

    @Test
    public void createPost() {
        // given
        // when
        // then
        assertThat(post.getUser()).isEqualTo(user);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getLikeCount()).isEqualTo(0L);
        assertThat(post.getComments()).isNotNull().isEmpty();
        assertThat(post.getPostMedias()).isNotNull().isEmpty();
    }

    @Test
    public void update() {
        // given
        String updateTitle = "제목 수정";
        String updateContent = "내용 수정";
        PostRequestDto requestDto = new PostRequestDto();
        requestDto.setTitle(updateTitle);
        requestDto.setContent(updateContent);

        // when
        post.update(requestDto);

        // then
        assertThat(post.getTitle()).isEqualTo(updateTitle);
        assertThat(post.getContent()).isEqualTo(updateContent);
    }
}