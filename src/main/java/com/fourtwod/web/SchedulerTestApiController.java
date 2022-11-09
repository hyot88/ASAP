package com.fourtwod.web;

import com.fourtwod.domain.mission.QMissionDetail;
import com.fourtwod.service.mission.MissionService;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import com.querydsl.core.Tuple;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"[임시] 스케줄러 테스트용 API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scheduler")
public class SchedulerTestApiController {

    private final MissionService missionService;

    @GetMapping
    @SuppressWarnings("all")
    public ApiResult schedulerTest() {
        // 어제 날짜 기준으로 종료된 미션 Tuple 조회
        List<Tuple> tupleList =  missionService.selectEndedMission();

        tupleList.forEach(tuple -> {
            long missionId = tuple.get(QMissionDetail.missionDetail.missionDetailId.missionDetailId);
            int sum = tuple.get(1, Integer.class);

            System.out.println(missionId);
            System.out.println(sum);
        });

        return new ApiResult<>(ResponseCode.COMM_S000);
    }
}
