package com.fourtwod.scheduler;

import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.mission.*;
import com.fourtwod.domain.user.Role;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.domain.user.UserRepository;
import com.fourtwod.scheduler.facade.SchedulerFacade;
import com.fourtwod.service.mission.MissionService;
import com.fourtwod.web.dto.MissionHistoryDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchedulerFacadeHalfMonthTest {

    @Autowired
    private SchedulerFacade schedulerFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionDetailRepository missionDetailRepository;

    @Autowired
    private MissionService missionService;

    // 테스트 비교값
    private String email = "test@google.com";
    private String registrationId = "google";
    private int dayCount = 15;

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
                .tier(0)
                .tierPoint(0)
                .role(Role.USER)
                .build());

        LocalDate lastLocalDate = LocalDate.now().minusMonths(1);
        int lastLocalDateYear = lastLocalDate.getYear();
        String lastMonth = String.format("%02d", lastLocalDate.getMonthValue());
        String lastMonthCondition = lastLocalDateYear + lastMonth;

        for (int day = 1; day <= dayCount; day++) {
            String strDate = lastMonthCondition + String.format("%02d", day);
            int missionType = 1;
            Mission mission = missionRepository.save(Mission.builder()
                    .missionType(missionType)
                    .date(strDate)
                    .proceeding(0)
                    .successFlag(1)
                    .user(user)
                    .build());

            missionDetailRepository.save(MissionDetail.builder()
                    .missionDetailId(MissionDetailId.builder()
                            .missionDetailId(mission.getMissionId())
                            .date(strDate)
                            .build())
                    .afternoon(1)
                    .night(1)
                    .mission(mission).build());
        }
    }

    @Test
    @Transactional
    public void scheduler_하프미션성공보상() {
        // 검증할 메소드
        schedulerFacade.monthSchedule();
        
        User user = userRepository.findById(UserId.builder()
                .email(email)
                .registrationId(registrationId)
                .build()).orElse(null);

        // 변경된 티어, 티어 포인트 검증
        assertThat(user.getTier()).isEqualTo(1);
        assertThat(user.getTierPoint()).isEqualTo(0);

        List<MissionHistoryDto> missionHistoryList = missionService.selectMissionHistory(SessionUser.builder()
                .email(email)
                .registrationId(registrationId)
                .build());
        MissionHistoryDto missionHistoryDto = missionHistoryList.get(0);

        // 히스토리 추가 여부 검증
        assertThat(missionHistoryDto.getMissionType()).startsWith("Huick").contains("15Day");
        assertThat(missionHistoryDto.getDate()).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        assertThat(missionHistoryDto.getTookCount()).isEqualTo(dayCount * 2);
        assertThat(missionHistoryDto.getChangeTierPoint()).isEqualTo(100);
    }
}
