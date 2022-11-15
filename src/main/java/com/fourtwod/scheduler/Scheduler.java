package com.fourtwod.scheduler;

import com.fourtwod.scheduler.facade.SchedulerFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final SchedulerFacade schedulerFacade;

    // 매일 0시 0분 0초
    @Scheduled(cron = "0 0 0 * * *")
    public void dailySchedule() {
        schedulerFacade.dailySchedule();
    }

    // 매달 15일 0시 30분 0초
    @Scheduled(cron = "0 30 0 15 * *")
    public void monthSchedule() {
        schedulerFacade.monthSchedule();
    }
}
