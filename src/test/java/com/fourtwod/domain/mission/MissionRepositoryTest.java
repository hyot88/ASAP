package com.fourtwod.domain.mission;

import com.fourtwod.domain.user.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MissionRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionDetailRepository missionDetailRepository;

    @Autowired
    private EntityManager entityManager;

    private JPAQueryFactory jpaQueryFactory;

    @Before
    public void before() {
        jpaQueryFactory = new JPAQueryFactory(entityManager);

        User user = userRepository.save(User.builder()
                .userId(UserId.builder()
                        .email("test@test.com")
                        .registrationId("google")
                        .build())
                .name("tester")
                .nickname("테스트123123")
                .role(Role.USER)
                .build());

        Mission mission = missionRepository.save(Mission.builder()
                .missionType(0)
                .user(user)
                .build());

        missionDetailRepository.save(MissionDetail.builder()
                .missionDetailId(MissionDetailId.builder()
                        .missionDetailId(mission.getMissionId())
                        .date("20221009")
                        .build())
                .afternoon(0)
                .night(0)
                .mission(mission)
                .build());
    }

    @Test
    public void test() {
        System.out.println("1. 미션 검색");
        Mission mission = missionRepository.findByUserAndProceeding(User.builder()
                .userId(UserId.builder()
                        .email("test@test.com")
                        .registrationId("google")
                        .build())
                .build(), 1)
                .orElse(null);
        
        System.out.println("1. 미션 검색 결과");
        System.out.println(mission);

        System.out.println("2. 미션 상세 검색");
        MissionDetail missionDetail = missionDetailRepository.findByMission(mission).orElse(null);

        System.out.println("2. 미션 상세 검색 결과");
        System.out.println(missionDetail);
    }

    @Test
    @Transactional
    public void test2() {
        QUser user = QUser.user;
        QMission mission = QMission.mission;
        QMissionDetail missionDetail = QMissionDetail.missionDetail;

        List<MissionDetail> list = jpaQueryFactory.selectFrom(missionDetail)
                .innerJoin(mission)
                    .on(missionDetail.missionDetailId.missionDetailId.eq(mission.missionId))
                .rightJoin(user)
                    .on(mission.user.userId.eq(user.userId))
                .where(user.userId.email.eq("test@test.com"))
                .fetch();

        System.out.println(list);
    }

    @Test
    public void test3() {
        String date = "20221103";
        int time = 1;
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime startLimit;
        LocalDateTime endLimit;

        if (time == 0) {
            startLimit = localDateTime.withHour(6).withMinute(0).withSecond(0).withNano(0);
            endLimit = localDateTime.withHour(12).withSecond(0).withSecond(30).withNano(0);
        } else {
            startLimit = localDateTime.withHour(18).withMinute(0).withSecond(0).withNano(0);
            endLimit = localDateTime.plusDays(1).withHour(0).withSecond(0).withSecond(30).withNano(0);
        }

        System.out.println(localDateTime);

        if (localDateTime.isAfter(startLimit) && localDateTime.isBefore(endLimit)) {
            System.out.println("신청 가능");
        } else {
            System.out.println("신청 불가능");
        }
    }
}
