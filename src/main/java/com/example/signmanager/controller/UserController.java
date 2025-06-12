package com.example.signmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * user 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor // 'final'로 선언된 필드(userService)를 초기화하는 생성자를 자동으로 만들어 줍니다. (의존성 주입)
public class UserController {}
