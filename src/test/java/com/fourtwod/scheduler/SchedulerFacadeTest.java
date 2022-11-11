package com.fourtwod.scheduler;

import com.fourtwod.scheduler.facade.SchedulerFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchedulerFacadeTest {

    @Autowired
    private SchedulerFacade schedulerFacade;

    @Test
    public void test() {
        schedulerFacade.dailySchedule();
    }
}
