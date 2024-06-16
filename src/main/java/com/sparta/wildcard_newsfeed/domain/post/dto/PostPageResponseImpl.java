package com.sparta.wildcard_newsfeed.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostPageResponseImpl implements PostPageResponseDto {
    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long likeCount;
}