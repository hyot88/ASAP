package com.fourtwod.web;

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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class MissionApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @SpyBean
    private Clock clock;

    private String email = "test@google.com";
    private String registrationId = "google";

    @Before
    @Transactional
    public void before() {
        // 사용자 저장
        User user = userRepository.save(User.builder()
                .userId(UserId.builder()
                        .email(email)
                        .registrationId(registrationId)
                        .build())
                .name("테스터")
                .nickname("테스터123")
                .tier(17)
                .tierPoint(81)
                .role(Role.USER)
                .build());
    }

    @Test
    @Transactional
    public void user_진행중인미션조회_미션생성_Took가능여부() throws Exception {
        // HttpSession 세팅
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", SessionUser.builder()
                .email(email)
                .registrationId(registrationId)
                .build());

        // 진행중인 미션 조회 (미션 없을 때)
        mvc.perform(get("/api/mission")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_S000.getCode())))
                .andExpect(jsonPath("$.data.missionType", is(-1)))
                .andExpect(jsonPath("$.data.detail", hasSize(0)));

        // 미션 생성 (미션 타입 오류)
        mvc.perform(post("/api/mission/15")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_E002.getCode())));

        // 미션 생성 (3일)
        LocalDate localDate = LocalDate.now();
        mvc.perform(post("/api/mission/3")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.missionType", is(3)))
                .andExpect(jsonPath("$.data.detail", hasSize(3)))
                .andExpect(jsonPath("$.data.detail[0].date", is(localDate.format(DateTimeFormatter.BASIC_ISO_DATE))))
                .andExpect(jsonPath("$.data.detail[0].afternoon", is(0)))
                .andExpect(jsonPath("$.data.detail[0].night", is(0)))
                .andExpect(jsonPath("$.data.detail[1].date", is(localDate.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE))))
                .andExpect(jsonPath("$.data.detail[1].afternoon", is(0)))
                .andExpect(jsonPath("$.data.detail[1].night", is(0)))
                .andExpect(jsonPath("$.data.detail[2].date", is(localDate.plusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE))))
                .andExpect(jsonPath("$.data.detail[2].afternoon", is(0)))
                .andExpect(jsonPath("$.data.detail[2].night", is(0)));

        // 진행중인 미션 조회 (미션 있을 때)
        mvc.perform(get("/api/mission")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.missionType", is(3)))
                .andExpect(jsonPath("$.data.detail", hasSize(3)))
                .andExpect(jsonPath("$.data.detail[0].date", is(localDate.format(DateTimeFormatter.BASIC_ISO_DATE))))
                .andExpect(jsonPath("$.data.detail[0].afternoon", is(0)))
                .andExpect(jsonPath("$.data.detail[0].night", is(0)))
                .andExpect(jsonPath("$.data.detail[1].date", is(localDate.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE))))
                .andExpect(jsonPath("$.data.detail[1].afternoon", is(0)))
                .andExpect(jsonPath("$.data.detail[1].night", is(0)))
                .andExpect(jsonPath("$.data.detail[2].date", is(localDate.plusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE))))
                .andExpect(jsonPath("$.data.detail[2].afternoon", is(0)))
                .andExpect(jsonPath("$.data.detail[2].night", is(0)));

        // 미션 생성 (중복)
        mvc.perform(post("/api/mission/3")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.MISS_E000.getCode())));

        ZoneOffset zoneOffset = ZoneOffset.of("+09:00");

        // clock을 참여안되는 오전 타임으로 모킹
        given(clock.instant())
                .willReturn(localDate.atTime(5, 0).toInstant(zoneOffset));

        // Took 던지기
        mvc.perform(patch("/api/mission/" + localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "/0")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.MISS_E001.getCode())));

        // clock을 참여되는 오전 타임으로 모킹
        given(clock.instant())
                .willReturn(localDate.atTime(6, 0).toInstant(zoneOffset));

        // Took 던지기
        mvc.perform(patch("/api/mission/" + localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "/0")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_S000.getCode())));

        // clock을 참여안되는 오후 타임으로 모킹
        given(clock.instant())
                .willReturn(localDate.plusDays(1).atTime(0, 1).toInstant(zoneOffset));

        // Took 던지기
        mvc.perform(patch("/api/mission/" + localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.MISS_E001.getCode())));

        // clock을 참여되는 오후 타임으로 모킹
        given(clock.instant())
                .willReturn(localDate.atTime(21, 0).toInstant(zoneOffset));

        // Took 던지기
        mvc.perform(patch("/api/mission/" + localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_S000.getCode())));
    }

    @Test
    @Transactional
    public void user_Took파라미터_미션참여변경여부() throws Exception {
        // HttpSession 세팅
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", SessionUser.builder()
                .email(email)
                .registrationId(registrationId)
                .build());

        LocalDate localDate = LocalDate.now();
        ZoneOffset zoneOffset = ZoneOffset.of("+09:00");

        // date 체크 오류
        mvc.perform(patch("/api/mission/202211211/0")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_E002.getCode())));

        // time 체크 오류
        mvc.perform(patch("/api/mission/20221121/2")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.COMM_E002.getCode())));


        // clock을 참여되는 오전 타임으로 모킹
        given(clock.instant())
                .willReturn(localDate.atTime(6, 0).toInstant(zoneOffset));

        // Took 던지기 (미션이 없어서 변경 이력이 없음)
        mvc.perform(patch("/api/mission/" + localDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "/0")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(ResponseCode.MISS_E002.getCode())));
    }
}
