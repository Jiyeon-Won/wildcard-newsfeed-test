package com.sparta.wildcard_newsfeed.domain.comment.entity;

import com.sparta.wildcard_newsfeed.domain.post.dto.PostRequestDto;
import com.sparta.wildcard_newsfeed.domain.post.entity.Post;
import com.sparta.wildcard_newsfeed.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new User("testId1234", "currentPWD999!", "test@gmail.com");

        post = new Post(
                new PostRequestDto("제목", "내용", null),
                user
        );

        comment = new Comment("댓글", user, post);
    }

    @Test
    void validComment() {
        // given
        // when
        // then
        assertThat(comment.getContent()).isEqualTo("댓글");
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getLikeCount()).isEqualTo(0L);
    }

    @Test
    void updateComment() {
        // given
        String updatedContent = "댓글 수정";

        // when
        comment.update(updatedContent);

        // then
        assertThat(comment.getContent()).isEqualTo(updatedContent);
    }
}