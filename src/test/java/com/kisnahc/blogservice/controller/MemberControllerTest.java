package com.kisnahc.blogservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.domain.Role;
import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.LoginMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.UpdateMemberRequest;
import com.kisnahc.blogservice.dto.response.LoginMemberResponse;
import com.kisnahc.blogservice.exception.member.DuplicateEmailException;
import com.kisnahc.blogservice.exception.member.DuplicateNicknameException;
import com.kisnahc.blogservice.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void create_member_test_success() throws Exception {
        CreateMemberRequest member = getCreateMemberRequest("memberA@gmail.com", "memberA", "MemberA123!");
        String body = toBody(member);

        mockMvc.perform(post("/api/auth/sign-up")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void validate_email_create_member_test() throws Exception {
        CreateMemberRequest member = getCreateMemberRequest("memberAgmail.com", "memberA", "MemberA123!");
        String body = toBody(member);

        mockMvc.perform(post("/api/auth/sign-up")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.message").value("Please enter it in the format of your email."))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorField", hasKey("email")))
                .andExpect(jsonPath("$.errorField", hasValue(member.getEmail())))
                .andDo(print());
    }

    @Test
    void validate_nickname_create_member_test() throws Exception {
        CreateMemberRequest member = getCreateMemberRequest("memberA@gmail.com", "A", "MemberA123!");
        String body = toBody(member);

        mockMvc.perform(post("/api/auth/sign-up")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.message").value("Please enter no more than 6 and no more than 20 characters."))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorField", hasKey("nickname")))
                .andExpect(jsonPath("$.errorField", hasValue(member.getNickname())))
                .andDo(print());
    }

    @Test
    void validate_password_create_member_test() throws Exception {
        /*
            password -> 영문(대, 소문자), 숫자, 특수문자를 포함한 8자 이상 20자 이하.
         */
        CreateMemberRequest member = getCreateMemberRequest("memberA@gmail.com", "memberA", "member1234");
        String body = toBody(member);

        mockMvc.perform(post("/api/auth/sign-up")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.message").value("must contain at least 8 to 20 characters, numbers, and special characters."))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errorField", hasKey("password")))
                .andExpect(jsonPath("$.errorField", hasValue(member.getPassword())))
                .andDo(print());
    }

    @Test
    void validate_duplicate_nickname_create_member_test() throws Exception {
        CreateMemberRequest member = getCreateMemberRequest("memberA@gmail.com", "memberA", "member1234!");
        String bodyA = toBody(member);

        mockMvc.perform(post("/api/auth/sign-up")
                        .content(bodyA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        CreateMemberRequest duplicateNicknameMember = getCreateMemberRequest("memberB@gmail.com", "memberA", "member1234!");
        String bodyB = toBody(duplicateNicknameMember);

        mockMvc.perform(post("/api/auth/sign-up")
                        .content(bodyB)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateNicknameException))
                .andExpect(jsonPath("$.message").value("This nickname is already in use."))
                .andDo(print());
    }

    @Test
    void validate_duplicate_email_create_member_test() throws Exception {
        CreateMemberRequest memberA = getCreateMemberRequest("memberA@gmail.com", "memberA", "member1234!");
        CreateMemberRequest memberB = getCreateMemberRequest("memberA@gmail.com", "memberB", "member1234!");

        String bodyA = toBody(memberA);
        String bodyB = toBody(memberB);

        mockMvc.perform(post("/api/auth/sign-up")
                        .content(bodyA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        mockMvc.perform(post("/api/auth/sign-up")
                        .content(bodyB)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This email is already in use."))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateEmailException))
                .andDo(print());
    }

    @Test
    void login_member_test() throws Exception {
        // 회원 가입.
        CreateMemberRequest memberA = getCreateMemberRequest("memberA@gmail.com", "memberA", "member1234!");
        String createRequestBody = toBody(memberA);
        mockMvc.perform(post("/api/auth/sign-up")
                        .content(createRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        // 로그인.
        LoginMemberRequest loginRequest = getLoginMemberRequest("memberA@gmail.com", "member1234!");

        String loginRequestBody = toBody(loginRequest);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void unauthorized_test() throws Exception {
        // 인증이 되지 않은 회원
        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.error").value("unauthorized"))
                .andDo(print());
    }

    @Test
    void authentication_member_test() throws Exception {
        // 회원 가입.
        CreateMemberRequest memberA = getCreateMemberRequest("memberA@gmail.com", "memberA", "member1234!");
        String createRequestBody = toBody(memberA);
        mockMvc.perform(post("/api/auth/sign-up")
                        .content(createRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        // 로그인.
        LoginMemberRequest loginRequest = getLoginMemberRequest("memberA@gmail.com", "member1234!");
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);

        // 인종된 회원 확인.
        mockMvc.perform(get("/api/auth/test").header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect(status().isOk());
    }

    @Test
    void authorization_member_test_fail() throws Exception {
        // 회원 가입.
        CreateMemberRequest memberA = getCreateMemberRequest("memberA@gmail.com", "memberA", "member1234!");
        String createRequestBody = toBody(memberA);
        mockMvc.perform(post("/api/auth/sign-up")
                        .content(createRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());


        // 로그인.
        LoginMemberRequest loginRequest = getLoginMemberRequest(memberA.getEmail(), memberA.getPassword());
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();


        Member member = memberRepository.findByEmail(loginRequest.getEmail()).get();

        member.setRole(Role.ROLE_MEMBER); // 권한 병경 -> ADMIN to MEMBER

        // ADMIN 접근 가능 .
        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);
        mockMvc.perform(get("/api/auth/test").header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("forbidden"))
                .andExpect(jsonPath("$.message").value("Access Denied"))
                .andExpect(jsonPath("$.status").value(403))
                .andDo(print());
    }

    @Test
    void authorization_member_test_success() throws Exception {
        // 회원 가입.
        CreateMemberRequest memberA = getCreateMemberRequest("memberA@gmail.com", "memberA", "member1234!");
        String createRequestBody = toBody(memberA);
        mockMvc.perform(post("/api/auth/sign-up")
                        .content(createRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());


        // 로그인.
        LoginMemberRequest loginRequest = getLoginMemberRequest(memberA.getEmail(), memberA.getPassword());
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();


        // ADMIN 접근 가능 .
        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);
        mockMvc.perform(get("/api/auth/test").header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void update_member_test_success() throws Exception {
        // 회원 가입.
        CreateMemberRequest memberA = getCreateMemberRequest("memberA@gmail.com", "memberA", "member1234!");
        String createRequestBody = toBody(memberA);
        mockMvc.perform(post("/api/auth/sign-up")
                        .content(createRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());


        // 로그인.
        LoginMemberRequest loginRequest = getLoginMemberRequest(memberA.getEmail(), memberA.getPassword());
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);

        Member member = memberRepository.findByEmail(loginRequest.getEmail()).get();

        // 회원 정보 수정.
        UpdateMemberRequest updateMemberRequest = getUpdateMemberRequest("updatedNickname");
        String body = toBody(updateMemberRequest);

        mockMvc.perform(patch("/api/members/{memberId}", member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.updatedNickname").value(updateMemberRequest.getNickname()))
                .andDo(print());
    }

    /* util */
    private String toBody(Object member) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(member);
    }

    private <T> T toObject(MvcResult mvcResult, Class<T> resultObject) throws UnsupportedEncodingException, JsonProcessingException {
        String content = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, resultObject);
    }


    /* dto */
    private CreateMemberRequest getCreateMemberRequest(String email, String nickname, String password) {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setEmail(email);
        createMemberRequest.setNickname(nickname);
        createMemberRequest.setPassword(password);
        return createMemberRequest;
    }

    private LoginMemberRequest getLoginMemberRequest(String email, String password) {
        LoginMemberRequest loginMemberRequest = new LoginMemberRequest();
        loginMemberRequest.setEmail(email);
        loginMemberRequest.setPassword(password);
        return loginMemberRequest;
    }

    private UpdateMemberRequest getUpdateMemberRequest(String nickname) {
        UpdateMemberRequest updateMemberRequest = new UpdateMemberRequest();
        updateMemberRequest.setNickname(nickname);
        return updateMemberRequest;
    }

}