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
                        .date("20221009")
                        .build())
                .afternoon(0)
                .night(0)
                .mission(mission)
                .build());
    }

    @Test
    public void test() {
//        Mission mission = missionRepository.findByUser(User.builder().userId(UserId.builder().email("test@test.com").registrationId("google").build()).build()).orElse(null);
//        System.out.println(mission.getMissionId());
//        System.out.println(mission.getMissionType());
    }
}
