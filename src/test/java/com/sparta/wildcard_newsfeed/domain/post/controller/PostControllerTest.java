package com.sparta.wildcard_newsfeed.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.wildcard_newsfeed.config.WebSecurityConfig;
import com.sparta.wildcard_newsfeed.domain.comment.dto.CommentResponseDto;
import com.sparta.wildcard_newsfeed.domain.comment.entity.Comment;
import com.sparta.wildcard_newsfeed.domain.comment.service.CommentService;
import com.sparta.wildcard_newsfeed.domain.file.service.FileService;
import com.sparta.wildcard_newsfeed.domain.mvc.MockSpringSecurityFilter;
import com.sparta.wildcard_newsfeed.domain.post.dto.*;
import com.sparta.wildcard_newsfeed.domain.post.entity.Post;
import com.sparta.wildcard_newsfeed.domain.post.repository.PostMediaRepository;
import com.sparta.wildcard_newsfeed.domain.post.repository.PostRepository;
import com.sparta.wildcard_newsfeed.domain.post.service.PostService;
import com.sparta.wildcard_newsfeed.domain.user.entity.User;
import com.sparta.wildcard_newsfeed.domain.user.entity.UserRoleEnum;
import com.sparta.wildcard_newsfeed.domain.user.entity.UserStatusEnum;
import com.sparta.wildcard_newsfeed.domain.user.repository.UserRepository;
import com.sparta.wildcard_newsfeed.security.AuthenticationUser;
import com.sparta.wildcard_newsfeed.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@WebMvcTest(
        controllers = {PostController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@TestPropertySource(properties = {"server.port=8080"})
class PostControllerTest {

    private MockMvc mvc;
    private Principal principal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    @MockBean
    CommentService commentService;

    private User user;

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
    public void addPost() throws Exception {
        // given
        this.mockUserSetUp();
        String title = "제목";
        String content = "내용";
        MockMultipartFile file1 = new MockMultipartFile("files", "image1.png", "image/png", new byte[]{});
        MockMultipartFile file2 = new MockMultipartFile("files", "image1.png", "image/png", new byte[]{});

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", title);
        params.add("content", content);

        // when
        PostRequestDto requestDto = new PostRequestDto(title, content, List.of(file1, file2));
        PostResponseDto responseDto = new PostResponseDto(
                new Post(requestDto, user),
                List.of(file1.getOriginalFilename(), file2.getOriginalFilename())
        );
        when(postService.addPost(any(PostRequestDto.class), any(AuthenticationUser.class))).thenReturn(responseDto);

        // then
        mvc.perform(multipart("/api/v1/post")
                        .file(file1)
                        .file(file2)
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시물 등록 성공"))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.username").value(responseDto.getUsername()))
                .andExpect(jsonPath("$.data.s3Urls[0]").value(responseDto.getS3Urls().get(0)));

        verify(postService).addPost(any(PostRequestDto.class), any(AuthenticationUser.class));
    }

    @Test
    public void findAll() throws Exception {
        // given
        PostRequestDto requestDto1 = new PostRequestDto("title1", "content1", null);
        PostRequestDto requestDto2 = new PostRequestDto("title2", "content2", null);
        User user = new User("testId1234", "currentPWD999!", "test@gamil.com");
        Post post1 = new Post(requestDto1, user);
        Post post2 = new Post(requestDto2, user);
        List<PostResponseDto> postList = List.of(new PostResponseDto(post1), new PostResponseDto(post2));

        // when
        when(postService.findAll()).thenReturn(postList);

        // then
        mvc.perform(get("/api/v1/post"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("게시물 전체 조회 성공"))
                .andExpect(jsonPath("$.data[0].title").value(postList.get(0).getTitle()))
                .andExpect(jsonPath("$.data[1].title").value(postList.get(1).getTitle()));

        verify(postService).findAll();
    }

    @Test
    public void findById() throws Exception {
        // given
        long postId = 1L;
        PostRequestDto requestDto = new PostRequestDto("title1", "content1", null);
        User user = new User("testId1234", "currentPWD999!", "test@gamil.com");
        Post post = new Post(requestDto, user);
        PostResponseDto postResponseDto = new PostResponseDto(post);

        Comment comment1 = new Comment("comment1", user, post);
        Comment comment2 = new Comment("comment2", user, post);
        List<CommentResponseDto> commentResponseDtoList = List.of(new CommentResponseDto(comment1), new CommentResponseDto(comment2));

        // when
        when(postService.findById(postId)).thenReturn(postResponseDto);
        when(commentService.findAllCommentsByPostId(postId)).thenReturn(commentResponseDtoList);

        // then
        mvc.perform(get("/api/v1/post/{postId}", postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("게시물 단일 조회, 댓글 조회 성공"))
                .andExpect(jsonPath("$.data.post.title").value(postResponseDto.getTitle()))
                .andExpect(jsonPath("$.data.post.content").value(postResponseDto.getContent()))
                .andExpect(jsonPath("$.data.comments[0].content").value(commentResponseDtoList.get(0).getContent()))
                .andExpect(jsonPath("$.data.comments[1].content").value(commentResponseDtoList.get(1).getContent()));

        verify(postService).findById(postId);
    }

    @Test
    public void updatePost() throws Exception {
        // given
        this.mockUserSetUp();
        User user = new User("testId1234", "currentPWD999!", "test@gamil.com");
        long postId = 1L;
        PostRequestDto requestDto = new PostRequestDto("update title", "update content", null);
        Post post = new Post(requestDto, user);
        MockMultipartFile file = new MockMultipartFile("files", "image.png", "image/png", new byte[]{});
        PostResponseDto responseDto = new PostResponseDto(post, List.of(file.getOriginalFilename()));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", requestDto.getTitle());
        params.add("content", requestDto.getContent());

        // when
        when(postService.updatePost(
                any(PostRequestDto.class),
                eq(postId),
                any(AuthenticationUser.class))
        ).thenReturn(responseDto);

        // then
        mvc.perform(multipart("/api/v1/post/{postId}", postId)
                        .file(file)
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andDo(print())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("게시물 수정 성공"))
                .andExpect(jsonPath("$.data.title").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.data.content").value(responseDto.getContent()))
                .andExpect(jsonPath("$.data.username").value(responseDto.getUsername()))
                .andExpect(jsonPath("$.data.s3Urls[0]").value(responseDto.getS3Urls().get(0)));

        verify(postService).updatePost(any(PostRequestDto.class), eq(postId), any(AuthenticationUser.class));
    }

    @Test
    public void deletePost() throws Exception {
        // given
        this.mockUserSetUp();
        long postId = 1L;

        // when
        doNothing().when(postService).deletePost(anyLong(), any(AuthenticationUser.class));

        // then
        mvc.perform(delete("/api/v1/post/{postId}", postId)
                        .principal(principal))
                .andDo(print())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("게시물 삭제 성공"));

        verify(postService).deletePost(anyLong(), any(AuthenticationUser.class));
    }

    @Test
    public void paging() throws Exception {
        // given
        int page = 1;
        int size = 10;
        String sortBy = "CREATE";
        String firstDate = "2024-06-01";
        String lastDate = "2024-06-30";
        PostPageRequestDto requestDto = new PostPageRequestDto();
        ReflectionTestUtils.setField(requestDto, "page", page);
        ReflectionTestUtils.setField(requestDto, "size", size);
        ReflectionTestUtils.setField(requestDto, "sortBy", sortBy);
        ReflectionTestUtils.setField(requestDto, "firstDate", firstDate);
        ReflectionTestUtils.setField(requestDto, "lastDate", lastDate);
        log.info("출력: {}", requestDto);

        PostPageResponseImpl responseDto = new PostPageResponseImpl(1L, 10L, "제목", "내용", "홍길동", LocalDateTime.now(), LocalDateTime.now(), 5L);

        List<PostPageResponseDto> content = Arrays.asList(responseDto, responseDto);
        Page<PostPageResponseDto> pageResponse = new PageImpl<>(content, PageRequest.of(page - 1, size), content.size());

        // when
        when(postService.getPostPage(any(PostPageRequestDto.class))).thenReturn(pageResponse);

        // then
        mvc.perform(post("/api/v1/post/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("게시물 페이지 조회 성공"))
                .andExpect(jsonPath("$.data.content[0].postId").value(1L))
                .andExpect(jsonPath("$.data.content[0].userId").value(10L))
                .andExpect(jsonPath("$.data.content[0].title").value("제목"))
                .andExpect(jsonPath("$.data.content[0].content").value("내용"))
                .andExpect(jsonPath("$.data.content[0].name").value("홍길동"));

        verify(postService).getPostPage(any(PostPageRequestDto.class));
    }
}