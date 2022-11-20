package com.fourtwod.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.mission.Mission;
import com.fourtwod.domain.mission.MissionRepository;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class UserApiControllerRankListTest1 {

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
        // 사용자/미션 저장
        Mission mission = missionRepository.save(Mission.builder()
                .missionType(3)
                .date("20221119")
                .proceeding(1)
                .successFlag(0)
                .user(userRepository.save(User.builder()
                        .userId(UserId.builder()
                                .email("auser@test.com")
                                .registrationId("google")
                                .build())
                        .name("a이름")
                        .nickname("a이름")
                        .tier(5)
                        .tierPoint(51)
                        .role(Role.USER)
                        .build()))
                .build());

        missionRepository.save(Mission.builder()
                .missionType(3)
                .date("20221119")
                .proceeding(1)
                .successFlag(0)
                .user(userRepository.save(User.builder()
                        .userId(UserId.builder()
                                .email("buser@test.com")
                                .registrationId("google")
                                .build())
                        .name("b이름")
                        .nickname("b이름")
                        .tier(7)
                        .tierPoint(43)
                        .role(Role.USER)
                        .build()))
                .build());

        missionRepository.save(Mission.builder()
                .missionType(3)
                .date("20221120")
                .proceeding(1)
                .successFlag(0)
                .user(userRepository.save(User.builder()
                        .userId(UserId.builder()
                                .email("cuser@test.com")
                                .registrationId("google")
                                .build())
                        .name("c이름")
                        .nickname("c이름")
                        .tier(2)
                        .tierPoint(23)
                        .role(Role.USER)
                        .build()))
                .build());

        missionRepository.save(Mission.builder()
                .missionType(5)
                .date("20221121")
                .proceeding(1)
                .successFlag(0)
                .user(userRepository.save(User.builder()
                        .userId(UserId.builder()
                                .email("duser@test.com")
                                .registrationId("google")
                                .build())
                        .name("d이름")
                        .nickname("d이름")
                        .tier(11)
                        .tierPoint(78)
                        .role(Role.USER)
                        .build()))
                .build());
    }

    @Test
    @Transactional
    public void user_랭크리스트조회1() throws Exception {
        // HttpSession 세팅
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", SessionUser.builder()
                .email("auser@test.com")
                .registrationId("google")
                .build());

        // 전체 랭크
        mvc.perform(get("/api/user/rank/0")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_S000.getCode())))
                .andExpect(jsonPath("$.data[0].ranking", is(1)))
                .andExpect(jsonPath("$.data[0].tier", is(11)))
                .andExpect(jsonPath("$.data[0].tierPoint", is(78)))
                .andExpect(jsonPath("$.data[0].nickname", is("d이름")))
                .andExpect(jsonPath("$.data[1].ranking", is(2)))
                .andExpect(jsonPath("$.data[1].tier", is(7)))
                .andExpect(jsonPath("$.data[1].tierPoint", is(43)))
                .andExpect(jsonPath("$.data[1].nickname", is("b이름")))
                .andExpect(jsonPath("$.data[2].ranking", is(3)))
                .andExpect(jsonPath("$.data[2].tier", is(5)))
                .andExpect(jsonPath("$.data[2].tierPoint", is(51)))
                .andExpect(jsonPath("$.data[2].nickname", is("a이름")))
                .andExpect(jsonPath("$.data[3].ranking", is(4)))
                .andExpect(jsonPath("$.data[3].tier", is(2)))
                .andExpect(jsonPath("$.data[3].tierPoint", is(23)))
                .andExpect(jsonPath("$.data[3].nickname", is("c이름")));
        
        // 진행중인 미션 랭크
        mvc.perform(get("/api/user/rank/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_S000.getCode())))
                .andExpect(jsonPath("$.data[0].ranking", is(1)))
                .andExpect(jsonPath("$.data[0].tier", is(7)))
                .andExpect(jsonPath("$.data[0].tierPoint", is(43)))
                .andExpect(jsonPath("$.data[0].nickname", is("b이름")))
                .andExpect(jsonPath("$.data[1].ranking", is(2)))
                .andExpect(jsonPath("$.data[1].tier", is(5)))
                .andExpect(jsonPath("$.data[1].tierPoint", is(51)))
                .andExpect(jsonPath("$.data[1].nickname", is("a이름")));
    }
}
