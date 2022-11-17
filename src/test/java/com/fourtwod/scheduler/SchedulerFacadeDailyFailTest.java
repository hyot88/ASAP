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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchedulerFacadeDailyFailTest {

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
    private int dayCount;

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

        dayCount = 5;
        LocalDate lastLocalDate = LocalDate.now().minusDays(dayCount);
        String date = lastLocalDate.format(DateTimeFormatter.BASIC_ISO_DATE);
        String strYearMonth = date.substring(0, 6);

        Mission mission = missionRepository.save(Mission.builder()
                .missionType(dayCount)
                .date(date)
                .proceeding(1)
                .successFlag(0)
                .user(user)
                .build());

        for (int day = 0; day < dayCount; day++) {
            int night = 1;

            // 마지막 미션의 night took을 못하도록 설정
            if (day == dayCount - 1) {
                night = 0;
            }

            String strDate = strYearMonth + String.format("%02d", lastLocalDate.plusDays(day).getDayOfMonth());
            missionDetailRepository.save(MissionDetail.builder()
                    .missionDetailId(MissionDetailId.builder()
                            .missionDetailId(mission.getMissionId())
                            .date(strDate)
                            .build())
                    .afternoon(1)
                    .night(night)
                    .mission(mission).build());
        }
    }

    @Test
    @Transactional
    public void scheduler_데일리_티어포인트계산_실패케이스() {
        // 검증할 메소드
        schedulerFacade.dailySchedule();
        
        User user = userRepository.findById(UserId.builder()
                .email(email)
                .registrationId(registrationId)
                .build()).orElse(null);

        // 변경된 티어, 티어 포인트 검증
        assertThat(user.getTier()).isEqualTo(13);
        assertThat(user.getTierPoint()).isEqualTo(31);

        List<MissionHistoryDto> missionHistoryList = missionService.selectMissionHistory(SessionUser.builder()
                .email(email)
                .registrationId(registrationId)
                .build());
        MissionHistoryDto missionHistoryDto = missionHistoryList.get(0);

        // 히스토리 추가 여부 검증
        assertThat(missionHistoryDto.getMissionType()).startsWith("Quick").contains("5Day");
        assertThat(missionHistoryDto.getDate()).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        assertThat(missionHistoryDto.getTookCount()).isEqualTo(9);
        assertThat(missionHistoryDto.getChangeTierPoint()).isEqualTo(-450);
    }
}
