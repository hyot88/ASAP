package com.fourtwod.scheduler.facade;

import com.fourtwod.service.mission.MissionService;
import com.fourtwod.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerFacade {

    private final MissionService missionService;
    private final UserService userService;

    public void dailySchedule() {
        System.out.println(missionService.selectEndedMission());
    }

    public void monthSchedule() {

    }
}
