package com.sparta.wildcard_newsfeed.domain.post.controller;

import com.sparta.wildcard_newsfeed.domain.common.CommonResponseDto;
import com.sparta.wildcard_newsfeed.domain.post.dto.PostRequestDto;
import com.sparta.wildcard_newsfeed.domain.post.dto.PostResponseDto;
import com.sparta.wildcard_newsfeed.domain.post.service.PostService;
import com.sparta.wildcard_newsfeed.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    // 게시물 등록
    @PostMapping
    public ResponseEntity<CommonResponseDto<PostResponseDto>> addPost(@Valid @RequestBody PostRequestDto postRequestDto, User user) {
        PostResponseDto postResponseDto = postService.addPost(postRequestDto, user);
        return ResponseEntity.ok()
                .body(CommonResponseDto.<PostResponseDto>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("게시물 등록 성공")
                        .data(postResponseDto)
                        .build());
    }

    // 게시물 전체 조회
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> findAll() {
        List<PostResponseDto> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    // 게시물 단일 조회
    @GetMapping("/{postid}")
    public ResponseEntity<PostResponseDto> findById(@PathVariable(name = "postid") long id) {
        PostResponseDto post = postService.findById(id);
        return ResponseEntity.ok(post);
    }
}