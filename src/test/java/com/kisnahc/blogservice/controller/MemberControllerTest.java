package com.kisnahc.blogservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.exception.member.DuplicateEmailException;
import com.kisnahc.blogservice.exception.member.DuplicateNicknameException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_member_test_success() throws Exception {

        CreateMemberRequest member = getMember("memberA@gmail.com", "memberA", "MemberA123!");

        String body = toBody(member);

        mockMvc.perform(post("/api/members")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void validate_email_create_member_test() throws Exception {
        CreateMemberRequest member = getMember("memberAgmail.com", "memberA", "MemberA123!");

        String body = toBody(member);

        mockMvc.perform(post("/api/members")
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
        CreateMemberRequest member = getMember("memberA@gmail.com", "A", "MemberA123!");

        String body = toBody(member);

        mockMvc.perform(post("/api/members")
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
        CreateMemberRequest member = getMember("memberA@gmail.com", "memberA", "member1234");

        String body = toBody(member);

        mockMvc.perform(post("/api/members")
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
        CreateMemberRequest memberA = getMember("memberA@gmail.com", "memberA", "member1234!");
        CreateMemberRequest memberB = getMember("memberB@gmail.com", "memberA", "member1234!");

        String bodyA = toBody(memberA);
        String bodyB = toBody(memberB);

        mockMvc.perform(post("/api/members")
                        .content(bodyA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        mockMvc.perform(post("/api/members")
                        .content(bodyB)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateNicknameException))
                .andExpect(jsonPath("$.message").value("This nickname is already in use."))
                .andDo(print());
    }

    @Test
    void validate_duplicate_email_create_member_test() throws Exception {
        CreateMemberRequest memberA = getMember("memberA@gmail.com", "memberA", "member1234!");
        CreateMemberRequest memberB = getMember("memberA@gmail.com", "memberB", "member1234!");

        String bodyA = toBody(memberA);
        String bodyB = toBody(memberB);

        mockMvc.perform(post("/api/members")
                        .content(bodyA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        mockMvc.perform(post("/api/members")
                        .content(bodyB)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This email is already in use."))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateEmailException))
                .andDo(print());
    }

    private String toBody(CreateMemberRequest member) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(member);
    }

    private CreateMemberRequest getMember(String email, String nickname, String password) {
        CreateMemberRequest createMemberRequest = new CreateMemberRequest();
        createMemberRequest.setEmail(email);
        createMemberRequest.setNickname(nickname);
        createMemberRequest.setPassword(password);
        return createMemberRequest;
    }

}