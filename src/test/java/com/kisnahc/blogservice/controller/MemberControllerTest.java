package com.kisnahc.blogservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisnahc.blogservice.domain.Member;
import com.kisnahc.blogservice.domain.Role;
import com.kisnahc.blogservice.dto.reqeust.CreateMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.LoginMemberRequest;
import com.kisnahc.blogservice.dto.reqeust.MemberSearchRequest;
import com.kisnahc.blogservice.dto.reqeust.UpdateMemberRequest;
import com.kisnahc.blogservice.dto.response.LoginMemberResponse;
import com.kisnahc.blogservice.exception.member.DuplicateEmailException;
import com.kisnahc.blogservice.exception.member.DuplicateNicknameException;
import com.kisnahc.blogservice.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void init() {
        memberRepository.deleteAll();
        this.entityManager
                .createNativeQuery("alter table member alter column `member_id` restart with 1")
                .executeUpdate();
    }


    @Test
    void create_member_test_success() throws Exception {
        createMember("memberA@gmail.com", "memberA", "MemberA123!");
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
        createMember("memberA@gmail.com", "memberA", "member1234!");
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
        createMember("memberA@gmail.com", "memberA", "member1234!");

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
        createMember("memberA@gmail.com", "memberA", "member1234!");

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
        CreateMemberRequest memberA = createMember( "memberA@gmail.com", "memberA", "member1234!");

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

        member.setRole(Role.ROLE_MANAGER); // 권한 변경 -> MEMBER to MANAGER

        // MEMBER 접근 가능 .
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
        CreateMemberRequest memberA = createMember("memberA@gmail.com", "memberA", "member1234!");

        // 로그인.
        LoginMemberRequest loginRequest = getLoginMemberRequest(memberA.getEmail(), memberA.getPassword());
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();


        // MEMBER 접근 가능.
        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);
        mockMvc.perform(get("/api/auth/test").header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void update_member_test_success() throws Exception {
        // 회원 가입.
        CreateMemberRequest memberA = createMember("memberA@gmail.com", "memberA", "member1234!");

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

    @Test
    void find_member_test() throws Exception{
        // 회원 가입.
        CreateMemberRequest memberA = createMember("memberA@gmail.com", "memberA", "member1234!");

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

        //회원 조회.
        mockMvc.perform(get("/api/members/{memberId}", member.getId())
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.role").value(member.getRole().toString()))
                .andExpect(jsonPath("$.createdDate").value(member.getCreatedDate().toString()))
                .andExpect(jsonPath("$.updatedDate").value(member.getUpdatedDate().toString()))
                .andDo(print());
    }

    @Test
    void find_members_dynamic_sorting_test() throws Exception{
        // 10명 회원 가입.
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Member member = Member.builder()
                    .email("member" + i + "@gmail.com")
                    .nickname("member" + i)
                    .role(Role.ROLE_MEMBER)
                    .password(passwordEncoder.encode("Member1234!"))
                    .build();
            members.add(member);
        }
        memberRepository.saveAll(members);

        // 로그인.
        LoginMemberRequest loginRequest = getLoginMemberRequest(members.get(0).getEmail(), "Member1234!");
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);

        //회원 조회.
        MemberSearchRequest memberSearchRequest = new MemberSearchRequest();
        memberSearchRequest.setSortBy("id");
        memberSearchRequest.setPage(1);
        memberSearchRequest.setSize(5);
        memberSearchRequest.setDirection(Sort.Direction.fromString("ASC"));

        mockMvc.perform(get("/api/members")
                        .param("page", String.valueOf(memberSearchRequest.getPage()))
                        .param("size", String.valueOf(memberSearchRequest.getSize()))
                        .param("sortBy", memberSearchRequest.getSortBy())
                        .param("direction", String.valueOf(memberSearchRequest.getDirection()))
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[4].memberId").value(5))
                .andDo(print());
    }

    @Test
    void find_members_dynamic_query_test() throws Exception{
        // 10명 회원 가입.
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Member member = Member.builder()
                    .email("member" + i + "@gmail.com")
                    .nickname("member" + i)
                    .role(Role.ROLE_MEMBER)
                    .password(passwordEncoder.encode("Member1234!"))
                    .build();
            members.add(member);
        }
        memberRepository.saveAll(members);

        // 로그인.
        LoginMemberRequest loginRequest = getLoginMemberRequest(members.get(0).getEmail(), "Member1234!");
        String loginRequestBody = toBody(loginRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn();

        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);

        //회원 조회.
        MemberSearchRequest memberSearchRequest = new MemberSearchRequest();
        memberSearchRequest.setSortBy("id");
        memberSearchRequest.setPage(1);
        memberSearchRequest.setSize(5);
        memberSearchRequest.setNicknameContains("1");
        memberSearchRequest.setDirection(Sort.Direction.fromString("ASC"));

        mockMvc.perform(get("/api/members")
                        .param("page", String.valueOf(memberSearchRequest.getPage()))
                        .param("size", String.valueOf(memberSearchRequest.getSize()))
                        .param("sortBy", memberSearchRequest.getSortBy())
                        .param("direction", String.valueOf(memberSearchRequest.getDirection()))
                        .param("nicknameContains", memberSearchRequest.getNicknameContains())
                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[1].memberId").value(10))
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

//    @Test
//    void find_members_test() throws Exception{
//        // 10명 회원 가입.
//        List<CreateMemberRequest> createMembers = new ArrayList<>();
//        for (int i = 1; i < 10; i++) {
//            createMembers.add(createMember("member" + i + "@gmail.com", "member" + i, "member1234!"));
//        }
//
//        // 로그인.
//        LoginMemberRequest loginRequest = getLoginMemberRequest(createMembers.get(0).getEmail(), createMembers.get(0).getPassword());
//        String loginRequestBody = toBody(loginRequest);
//        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(loginRequestBody))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn();
//
//        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);
//
//        //회원 조회.
//        MvcResult result = mockMvc.perform(get("/api/members")
//                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn();
//        List<MemberResponse> memberResponse = toObject(result, new ArrayList<MemberResponse>().getClass());
//
//        Assertions.assertThat(createMembers.size()).isEqualTo(memberResponse.size());
//    }

//    @Test
//    void find_members_pagination_test() throws Exception{
//        // 200명 회원 가입.
//        List<Member> members = new ArrayList<>();
//        for (int i = 1; i <= 200; i++) {
//            Member member = Member.builder()
//                    .email("member" + i + "@gmail.com")
//                    .nickname("member" + i)
//                    .role(Role.ROLE_MEMBER)
//                    .password(passwordEncoder.encode("Member1234!"))
//                    .build();
//            members.add(member);
//        }
//        memberRepository.saveAll(members);
//
//        // 로그인.
//        LoginMemberRequest loginRequest = getLoginMemberRequest(members.get(0).getEmail(), "Member1234!");
//        String loginRequestBody = toBody(loginRequest);
//        MvcResult mvcResult = mockMvc.perform(post("/api/auth/sign-in")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(loginRequestBody))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        LoginMemberResponse loginMemberResponse = toObject(mvcResult, LoginMemberResponse.class);
//
//        //회원 조회.
//        mockMvc.perform(get("/api/members?page=0&size=20")
//                        .header("Authorization", "Bearer " + loginMemberResponse.getJwt()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(20))
//                .andExpect(jsonPath("$[0].memberId").value(200))
//                .andExpect(jsonPath("$[19].memberId").value(181))
//                .andDo(print())
//                .andReturn();
//
//    }
}