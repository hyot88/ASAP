package com.fourtwod.web;

import com.fourtwod.scheduler.facade.SchedulerFacade;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"[임시] 스케줄러 테스트용 API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scheduler")
public class SchedulerTestApiController {

    private final SchedulerFacade schedulerFacade;

    @GetMapping("/day")
    public ApiResult schedulerDayTest() {
        schedulerFacade.dailySchedule();
        return new ApiResult<>(ResponseCode.COMM_S000);
    }

    @GetMapping("/month")
    public ApiResult schedulerMonthTest() {
        schedulerFacade.monthSchedule();
        return new ApiResult<>(ResponseCode.COMM_S000);
    }
}
