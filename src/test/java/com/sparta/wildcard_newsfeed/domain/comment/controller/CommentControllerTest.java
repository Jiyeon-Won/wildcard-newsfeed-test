package com.sparta.wildcard_newsfeed.domain.comment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.wildcard_newsfeed.config.WebSecurityConfig;
import com.sparta.wildcard_newsfeed.domain.comment.dto.CommentRequestDto;
import com.sparta.wildcard_newsfeed.domain.comment.dto.CommentResponseDto;
import com.sparta.wildcard_newsfeed.domain.comment.entity.Comment;
import com.sparta.wildcard_newsfeed.domain.comment.repository.CommentRepository;
import com.sparta.wildcard_newsfeed.domain.comment.service.CommentService;
import com.sparta.wildcard_newsfeed.domain.mvc.MockSpringSecurityFilter;
import com.sparta.wildcard_newsfeed.domain.post.controller.PostController;
import com.sparta.wildcard_newsfeed.domain.post.entity.Post;
import com.sparta.wildcard_newsfeed.domain.post.repository.PostRepository;
import com.sparta.wildcard_newsfeed.domain.user.entity.User;
import com.sparta.wildcard_newsfeed.domain.user.entity.UserRoleEnum;
import com.sparta.wildcard_newsfeed.domain.user.entity.UserStatusEnum;
import com.sparta.wildcard_newsfeed.domain.user.repository.UserRepository;
import com.sparta.wildcard_newsfeed.domain.user.service.UserService;
import com.sparta.wildcard_newsfeed.security.AuthenticationUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@WebMvcTest(
        controllers = {CommentController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@TestPropertySource(properties = {"server.port=8080"})
class CommentControllerTest {

    private MockMvc mvc;
    private Principal principal;
    private User user;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetUp() {
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
        AuthenticationUser userDetails = AuthenticationUser.of(user);
        principal = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Test
    public void addComment() throws Exception {
        // given
        this.mockUserSetUp();
        long postId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("comment");

        Post post = new Post();
        ReflectionTestUtils.setField(post, "id", postId);

        Comment comment = new Comment(requestDto.getContent(), user, post);
        CommentResponseDto responseDto = new CommentResponseDto(comment);

        // when
        when(commentService.addComment(eq(postId), any(CommentRequestDto.class), any(AuthenticationUser.class))).thenReturn(responseDto);

        // then
        mvc.perform(post("/api/v1/post/{postId}/comment", postId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                ).andDo(print())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("댓글 등록 성공"))
                .andExpect(jsonPath("$.data.content").value(responseDto.getContent()));

        verify(commentService).addComment(eq(postId), any(CommentRequestDto.class), any(AuthenticationUser.class));
    }

    @Test
    public void updateComment() throws Exception {
        // given
        this.mockUserSetUp();
        long postId = 1L;
        long commentId = 1L;

        CommentRequestDto requestDto = new CommentRequestDto("update comment");

        Post post = new Post();
        ReflectionTestUtils.setField(post, "id", postId);

        Comment comment = new Comment(requestDto.getContent(), user, post);
        CommentResponseDto responseDto = new CommentResponseDto(comment);

        // when
        when(commentService.updateComment(eq(postId), eq(commentId), any(CommentRequestDto.class), any(AuthenticationUser.class))).thenReturn(responseDto);

        // then
        mvc.perform(put("/api/v1/post/{postId}/comment/{commentId}", postId, commentId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal)
                ).andDo(print())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("댓글 수정 성공"))
                .andExpect(jsonPath("$.data.content").value(responseDto.getContent()));

        verify(commentService).updateComment(eq(postId), eq(commentId), any(CommentRequestDto.class), any(AuthenticationUser.class));
    }

    @Test
    public void deleteComment() throws Exception {
        // given
        this.mockUserSetUp();
        long postId = 1L;
        long commentId = 1L;

        // when
        doNothing().when(commentService).deleteComment(eq(postId), eq(commentId), anyString());

        // then
        mvc.perform(delete("/api/v1/post/{postId}/comment/{commentId}", postId, commentId)
                        .principal(principal)
                ).andDo(print())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("댓글 삭제 성공"));
    }
}