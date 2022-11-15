package com.fourtwod.scheduler;

import com.fourtwod.domain.mission.*;
import com.fourtwod.domain.user.Role;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.domain.user.UserRepository;
import com.fourtwod.scheduler.facade.SchedulerFacade;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.YearMonth;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchedulerFacadeMonthTest {

    @Autowired
    private SchedulerFacade schedulerFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionDetailRepository missionDetailRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Before
    public void before() {
        String email = "test@google.com";
        String registrationId = "google";

        // 사용자 저장
        userRepository.save(User.builder()
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
        int dayCount = YearMonth.from(lastLocalDate).lengthOfMonth();

        for (int day = 1; day < dayCount; day++) {
            missionRepository.save(Mission.builder()
                    .missionType(1)
                    .date(lastMonthCondition + String.format("%02d", day))
                    .proceeding(0)
                    .successFlag(1)
                    .user(User.builder()
                            .userId(UserId.builder()
                                    .email("")
                                    .registrationId("")
                                    .build())
                            .build())
                    .build());
            missionDetailRepository.save(MissionDetail.builder()
                            .missionDetailId(MissionDetailId.builder().build())
                            .afternoon(1)
                            .night(1)
                            .mission(Mission.builder().missionId(0l).build())
                    .build());
        }
    }

    @Test
    public void monthSchedule() {
        schedulerFacade.monthSchedule();
    }
}
