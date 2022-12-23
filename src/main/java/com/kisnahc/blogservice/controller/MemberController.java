package com.kisnahc.blogservice.controller;

import com.kisnahc.blogservice.auth.MemberAdapter;
import com.kisnahc.blogservice.dto.reqeust.LoginMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.LogoutMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.UpdateMemberRequest;
import com.kisnahc.blogservice.dto.response.*;
import com.kisnahc.blogservice.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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


    @PreAuthorize("#memberId == #memberAdapter.member.id")
    @PatchMapping("/api/members/{memberId}")
    public ResponseEntity<UpdateMemberResponse> updateMember(@PathVariable Long memberId,
                                                             @RequestBody @Valid UpdateMemberRequest request,
                                                             @AuthenticationPrincipal MemberAdapter memberAdapter) {
        UpdateMemberResponse memberResponse = memberService.update(memberId, request);
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

    @PreAuthorize("#memberId == #memberAdapter.member.id")
    @DeleteMapping("/api/members/{memberId}")
    public ResponseEntity<DeleteMemberResponse> deleteMember(@PathVariable Long memberId,
                                                             @AuthenticationPrincipal MemberAdapter memberAdapter) {
        DeleteMemberResponse memberResponse = memberService.delete(memberId);
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

    @GetMapping("/api/members/{memberId}")
    public ResponseEntity<MemberResponse> findMember(@PathVariable Long memberId) {
        MemberResponse memberResponse = memberService.findMember(memberId);
        return new ResponseEntity<>(memberResponse, HttpStatus.OK);
    }

    @GetMapping("/api/members")
    public ResponseEntity<List<MemberResponse>> findMembers() {
        List<MemberResponse> memberResponses = memberService.findMembers();
        return new ResponseEntity<>(memberResponses, HttpStatus.OK);
    }

    @GetMapping("/api/auth/test")
    public ResponseEntity<String> authTest() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
