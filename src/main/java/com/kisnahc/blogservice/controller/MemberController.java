package com.kisnahc.blogservice.controller;

import com.kisnahc.blogservice.dto.reqeust.LoginMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.LogoutMemberRequest;
import com.kisnahc.blogservice.dto.response.CreateMemberResponse;
import com.kisnahc.blogservice.dto.response.LoginMemberResponse;
import com.kisnahc.blogservice.dto.response.LogoutMemberResponse;
import com.kisnahc.blogservice.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/auth/sign-up")
    public ResponseEntity<CreateMemberResponse> saveMember(@RequestBody @Valid CreateMemberRequest request) {
        CreateMemberResponse memberResponse = memberService.save(request);
        return new ResponseEntity<>(memberResponse, HttpStatus.CREATED);
    }

    @PostMapping("/api/auth/sign-in")
    public ResponseEntity<LoginMemberResponse> loginMember(@RequestBody @Valid LoginMemberRequest request) {
        LoginMemberResponse memberResponse = memberService.login(request);
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<LogoutMemberResponse> logoutMember(@RequestBody LogoutMemberRequest request) {
        LogoutMemberResponse memberResponse = memberService.logout(request);
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

    @GetMapping("/api/auth/test")
    public ResponseEntity<String> authTest() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
