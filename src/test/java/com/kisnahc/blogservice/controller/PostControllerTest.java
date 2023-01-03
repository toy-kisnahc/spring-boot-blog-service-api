package com.kisnahc.blogservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.CreatePostRequest;
import com.kisnahc.blogservice.dto.reqeust.LoginMemberRequest;
import com.kisnahc.blogservice.dto.response.LoginMemberResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void create_post_test_success() throws Exception {
        // 회원가입 및 로그인.
        createMember("memberA@gmail.com", "memberA", "member1234!");
        LoginMemberRequest loginRequest = getLoginMemberRequest("memberA@gmail.com", "member1234!");
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();
        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);

        // 게시글 작성
        CreatePostRequest postRequest = getCreatePostRequest();

        String body = toBody(postRequest);
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect((jsonPath("$.author")).value("memberA"))
                .andExpect((jsonPath("$.title")).value("게시글 제목"))
                .andExpect((jsonPath("$.content")).value("게시글 내"))
                .andDo(print());
    }

    @Test
    void find_post_test() throws Exception {
        // 회원가입 및 로그인.
        createMember("memberA@gmail.com", "memberA", "member1234!");
        LoginMemberRequest loginRequest = getLoginMemberRequest("memberA@gmail.com", "member1234!");
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();
        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);

        // 게시글 작성
        createPost(loginMemberResponse);

        //게시글 조회
        mockMvc.perform(get("/api/posts/{postId}", 1L)
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect((status()).isOk())
                .andExpect(( jsonPath("$.title").value("게시글 제목")))
                .andExpect(( jsonPath("$.content").value("게시글 내용")))
                .andExpect(( jsonPath("$.view").value(1)))
                .andExpect(( jsonPath("$.author").value("memberA")))
                .andDo(print());

    }

    @Test
    void find_post_abusing_view_count_test() throws Exception {
        // 회원가입 및 로그인.
        createMember("memberA@gmail.com", "memberA", "member1234!");
        LoginMemberRequest loginRequest = getLoginMemberRequest("memberA@gmail.com", "member1234!");
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();
        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);

        // 게시글 작성
        createPost(loginMemberResponse);

        //게시글 조회
        MvcResult firstFindPost = mockMvc.perform(get("/api/posts/{postId}", 1L)
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect((status()).isOk())
                .andExpect((jsonPath("$.title").value("게시글 제목")))
                .andExpect((jsonPath("$.content").value("게시글 내용")))
                .andExpect((jsonPath("$.view").value(1)))
                .andExpect((jsonPath("$.author").value("memberA")))
                .andReturn();

        mockMvc.perform(get("/api/posts/{postId}", 1L)
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt())
                        .cookie(firstFindPost.getResponse().getCookie("postView")))
                .andExpect((status()).isOk())
                .andExpect(( jsonPath("$.view").value(1)))
                .andDo(print());

    }

    private void createPost(LoginMemberResponse loginMemberResponse) throws Exception {
        CreatePostRequest postRequest = getCreatePostRequest();

        String body = toBody(postRequest);
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect((jsonPath("$.author")).value("memberA"))
                .andExpect((jsonPath("$.title")).value("게시글 제목"))
                .andExpect((jsonPath("$.content")).value("게시글 내용"));
    }

    /* util */
    private String toBody(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    private <T> T toObject(MvcResult mvcResult, Class<T> resultObject) throws UnsupportedEncodingException, JsonProcessingException {
        String content = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, resultObject);
    }

    /* dto */
    private static CreatePostRequest getCreatePostRequest() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setTitle("게시글 제목");
        postRequest.setContent("게시글 내용");
        return postRequest;
    }

    private CreateMemberRequest createMember(String email, String nickname, String password) throws Exception {
        CreateMemberRequest memberA = getCreateMemberRequest(email, nickname, password);
        String createRequestBody = toBody(memberA);
        mockMvc.perform(post("/api/auth/sign-up")
                        .content(createRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
        return memberA;
    }

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

}