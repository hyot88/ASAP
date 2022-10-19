package com.fourtwod.domain.mission;

import com.fourtwod.domain.user.Role;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.domain.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

    @Before
    public void before() {
        System.out.println("비포 시작");
        User user = userRepository.save(User.builder()
                .userId(UserId.builder()
                        .email("test@test.com")
                        .registrationId("google")
                        .build())
                .name("tester")
                .nickname("테스트123123")
                .role(Role.GUEST)
                .build());

        Mission mission = missionRepository.save(Mission.builder()
                .missionType(0)
                .user(user)
                .build());

        missionDetailRepository.save(MissionDetail.builder()
                .missionDetailId(MissionDetailId.builder()
                        .missionDetailId(123l)
                        .date("20221009")
                        .build())
                .afternoon(0)
                .night(0)
                .mission(mission)
                .build());
        System.out.println("비포 종료");
    }

    @Test
    public void test() {
        System.out.println("1. 미션 검색");
        Mission mission = missionRepository.findByUser(User.builder()
                .userId(UserId.builder()
                        .email("test@test.com")
                        .registrationId("google")
                        .build())
                .build())
                .orElse(null);
        
        System.out.println("1. 미션 검색 결과");
        System.out.println(mission);

        System.out.println("2. 미션 상세 검색");
        MissionDetail missionDetail = missionDetailRepository.findByMission(mission).orElse(null);

        System.out.println("2. 미션 상세 검색 결과");
        System.out.println(missionDetail);
    }
}
