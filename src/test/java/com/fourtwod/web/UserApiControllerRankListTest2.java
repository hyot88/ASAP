package com.fourtwod.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.mission.Mission;
import com.fourtwod.domain.mission.MissionRepository;
import com.fourtwod.domain.user.Role;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.domain.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class UserApiControllerRankListTest2 {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Before
    @Transactional
    public void before() {
        char c = 97;

        for (int i = 0; i < 25; i++) {
            char cTemp = (char)(c + i);
            userRepository.save(User.builder()
                    .userId(UserId.builder()
                            .email(cTemp + "user@test.com")
                            .registrationId("google")
                            .build())
                    .name(cTemp + "이름")
                    .nickname(cTemp + "이름")
                    .tier(0)
                    .tierPoint(i)
                    .role(Role.USER)
                    .build());
        }
    }

    @Test
    public void user_랭크리스트조회2() throws Exception {
        // HttpSession 세팅
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", SessionUser.builder()
                .email("buser@test.com")
                .registrationId("google")
                .build());

        // 최대 20등 범위를 벗어난 유저의 전체 랭크 리스트 조회
        mvc.perform(get("/api/user/rank/0")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(21)))
                .andExpect(jsonPath("$.data[19].nickname", is("f이름")))
                .andExpect(jsonPath("$.data[20].ranking", is(24)))
                .andExpect(jsonPath("$.data[20].nickname", is("b이름")));
        
        session.setAttribute("user", SessionUser.builder()
                .email("kuser@test.com")
                .registrationId("google")
                .build());

        // 최대 20등 범위 안에 있는 유저의 전체 랭크 리스트 조회
        mvc.perform(get("/api/user/rank/0")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(20)))
                .andExpect(jsonPath("$.data[14].nickname", is("k이름")));
    }
}
