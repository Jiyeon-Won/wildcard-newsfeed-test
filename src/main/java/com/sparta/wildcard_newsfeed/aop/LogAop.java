package com.sparta.wildcard_newsfeed.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "Request URL, Method")
@Aspect
@Component
public class LogAop {

//    @Pointcut("execution(* com.sparta.wildcard_newsfeed.domain.comment.controller.CommentController.*(..))")
//    private void comment() {}
//    @Pointcut("execution(* com.sparta.wildcard_newsfeed.domain.liked.controller.LikedController.*(..))")
//    private void liked() {}
//    @Pointcut("execution(* com.sparta.wildcard_newsfeed.domain.post.controller.PostController.*(..))")
//    private void post() {}
//    @Pointcut("execution(* com.sparta.wildcard_newsfeed.domain.token.controller.TokenController.*(..))")
//    private void token() {}
//    @Pointcut("execution(* com.sparta.wildcard_newsfeed.domain.user.controller.UserController.*(..))")
//    private void user() {}
    @Pointcut("execution(* com.sparta.wildcard_newsfeed.domain..*Controller.*(..))")
    private void allController() {}

//    @Pointcut("execution(* com.sparta.wildcard_newsfeed.security.jwt.JwtAuthenticationFilter.attemptAuthentication(..))")
//    private void login() {} //안된다.

    /*
    1번
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    log.info("Request URL: {}, HTTP Method: {}", request.getRequestURL(), request.getMethod());
    currentRequestAttributes 는 요청이 없을 경우 예외 던짐

    2번
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();

        log.info("Request URL: {}, HTTP Method: {}", request.getRequestURL(), request.getMethod());
    }
    getRequestAttributes 는 요청이 없을경우 null 반환
     */
    @Before("allController()")
    public void logging() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("Request URL: {}, HTTP Method: {}", request.getRequestURL(), request.getMethod());
    }
}