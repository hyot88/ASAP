package com.fourtwod.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.user.Role;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.domain.user.UserRepository;
import com.fourtwod.web.handler.ResponseCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class UserApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    // 테스트 비교값
    private String email = "test@google.com";
    private String registrationId = "google";
    private String name = "테스터";

    @Before
    @Transactional
    public void before() {
        // 사용자 저장
        User user = userRepository.save(User.builder()
                .userId(UserId.builder()
                        .email(email)
                        .registrationId(registrationId)
                        .build())
                .name(name)
                .tier(17)
                .tierPoint(81)
                .role(Role.USER)
                .build());
    }
    
    @Test
    public void user_닉네임체크() throws Exception {
        // request body
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("nickname", "test");

        // HttpSession 세팅
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", SessionUser.builder()
                .email(email + "123")
                .registrationId(registrationId)
                .build());

        // 잘못된 flag로 요청올 경우
        mvc.perform(patch("/api/user/nickname/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramMap))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_E001.getCode())));

        // Email이 검색되지 않을 경우
        mvc.perform(patch("/api/user/nickname/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramMap))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_E001.getCode())));

        // 정상
        session.setAttribute("user", SessionUser.builder()
                .email(email)
                .registrationId(registrationId)
                .build());
        mvc.perform(patch("/api/user/nickname/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramMap))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_S000.getCode())));

        // 2자 이상 10자 이하 닉네임이 아닌 경우
        paramMap.put("nickname", "일이삼사오육칠팔구십십일");
        mvc.perform(patch("/api/user/nickname/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramMap))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.NICK_E000.getCode())));

        // 한글,영문,숫자 닉네임이 아닌 경우
        paramMap.put("nickname", "!@#$%");
        mvc.perform(patch("/api/user/nickname/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramMap))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.NICK_E001.getCode())));

        // 닉네임이 중복될 경우
        paramMap.put("nickname", "test");
        mvc.perform(patch("/api/user/nickname/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramMap))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.NICK_E002.getCode())));

        // 사용할 수 없는 로그인 수단인 경우
        session.setAttribute("user", SessionUser.builder()
                .email(email)
                .registrationId(registrationId + 123)
                .build());
        mvc.perform(patch("/api/user/nickname/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paramMap))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_E001.getCode())));
    }
}
