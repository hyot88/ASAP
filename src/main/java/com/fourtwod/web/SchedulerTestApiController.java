package com.fourtwod.web;

import com.fourtwod.domain.mission.QMission;
import com.fourtwod.domain.user.User;
import com.fourtwod.service.MissionInfo;
import com.fourtwod.service.TierInfo;
import com.fourtwod.service.mission.MissionService;
import com.fourtwod.service.user.UserService;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import com.querydsl.core.Tuple;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Api(tags = {"[임시] 스케줄러 테스트용 API"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scheduler")
public class SchedulerTestApiController {

    private final MissionService missionService;
    private final UserService userService;

    @GetMapping
    @SuppressWarnings("all")
    public ApiResult schedulerTest() {
        // 어제 날짜 기준으로 종료된 미션 Tuple 조회
        List<Tuple> tupleList =  missionService.selectEndedMission();

        //TODO: 테스트를 위해서 임시로 yesterDay를 조정
//        String yesterDay = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        String yesterDay = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        tupleList.forEach(tuple -> {
            long missionId = tuple.get(QMission.mission.missionId);
            int missionType = tuple.get(QMission.mission.missionType);
            String endDate = tuple.get(2, String.class);
            int sum = tuple.get(3, Integer.class);

            LocalDateTime currentLocalDateTime = LocalDate.parse(yesterDay, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay();
            LocalDateTime endLocalDateTime = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay();
            // 미션 몇번째 날인지 계산
            int missionOrder = missionType - Long.valueOf(Duration.between(endLocalDateTime, currentLocalDateTime).toDays()).intValue();
            // 미션 성공 여부 체크
            int successFlag = (missionOrder * 2) == sum ? 1 : 0;

            // 1. 미션 성공으로 완료 시
            // 2. 미션 실패 시
            if ((missionType == missionOrder && successFlag == 1)
                    || successFlag == 0) {

                // mission proceeding 0 변경
                // change tier, tierPoint

                User user = userService.selectUserInfo(missionId);
                int tier = user.getTier();
                int tierPoint = user.getTierPoint();
                int beforeTierPoint = tier * 100 + tierPoint;
                int afterTierPoint;
                MissionInfo missionInfo = MissionInfo.find(missionType);
                int rewardPoint = missionInfo.getRewardPoint();

                // 미션 성공
                if (successFlag == 1) {
                    afterTierPoint = beforeTierPoint + rewardPoint;
                } else {
                    TierInfo beforeTierInfo = TierInfo.findByTier(tier);
                    afterTierPoint = beforeTierPoint + (int) Math.ceil(rewardPoint * beforeTierInfo.getPenalty() * -1);
                }

                TierInfo afterTierInfo = TierInfo.findByTierPoint(afterTierPoint);
                System.out.println("afterTier: " + afterTierInfo.getTier());
                System.out.println("afterTierPoint: " + (afterTierPoint - (afterTierPoint / 100 * 100)));

            }
        });

        return new ApiResult<>(ResponseCode.COMM_S000);
    }
}
