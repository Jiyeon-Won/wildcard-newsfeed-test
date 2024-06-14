package com.sparta.wildcard_newsfeed.domain.user.service;

import com.sparta.wildcard_newsfeed.domain.user.dto.UserEmailRequestDto;
import com.sparta.wildcard_newsfeed.domain.user.entity.AuthCodeHistory;
import com.sparta.wildcard_newsfeed.domain.user.entity.User;
import com.sparta.wildcard_newsfeed.domain.user.entity.UserRoleEnum;
import com.sparta.wildcard_newsfeed.domain.user.repository.AuthCodeRepository;
import com.sparta.wildcard_newsfeed.domain.user.repository.UserRepository;
import com.sparta.wildcard_newsfeed.exception.customexception.AuthCodeExpireException;
import com.sparta.wildcard_newsfeed.exception.customexception.AuthCodeNoMatchException;
import com.sparta.wildcard_newsfeed.exception.customexception.AuthCodeNotFoundException;
import com.sparta.wildcard_newsfeed.exception.customexception.UserNotFoundException;
import com.sparta.wildcard_newsfeed.security.AuthenticationUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class AuthCodeService {

    private final AuthCodeRepository authCodeRepository;
    private final UserRepository userRepository;
    /**
     * 조회 및 검증
     */
    @Transactional
    public void verifyAuthCode(AuthenticationUser loginUser, UserEmailRequestDto requestDto) {
        User findUser = userRepository.findByUsercode(loginUser.getUsername()).orElseThrow(UserNotFoundException::new);

        AuthCodeHistory findAuthCode = authCodeRepository.findTop1ByUserAndAutoCodeOrderByExpireDateDesc(findUser, requestDto.getAuthCode())
                .orElseThrow(AuthCodeNotFoundException::new);

        if (!requestDto.getAuthCode().equals(findAuthCode.getAutoCode())) {
            throw new AuthCodeNoMatchException();
        }

        if (!findAuthCode.getUser().equals(findAuthCode.getUser())) {
            throw new AuthCodeNoMatchException();
        }

        if (findAuthCode.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new AuthCodeExpireException();
        }
        findUser.updateUserStatus();
    }

}