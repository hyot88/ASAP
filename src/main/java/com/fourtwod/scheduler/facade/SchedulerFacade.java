package com.fourtwod.scheduler.facade;

import com.fourtwod.domain.mission.MissionHistory;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.service.MissionInfo;
import com.fourtwod.service.TierInfo;
import com.fourtwod.service.mission.MissionService;
import com.fourtwod.service.user.UserService;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.fourtwod.domain.mission.QMission.mission;

@Component
@RequiredArgsConstructor
public class SchedulerFacade {

    private final MissionService missionService;
    private final UserService userService;

    @SuppressWarnings("all")
    public void dailySchedule() {
        // 어제 날짜 기준으로 종료된 미션 Tuple 조회
        List<Tuple> tupleList =  missionService.selectEndedMission();
        String yesterDay = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

        tupleList.forEach(tuple -> {
            if (tuple != null) {
                long missionId = tuple.get(mission.missionId);
                int missionType = tuple.get(mission.missionType);
                String endDate = tuple.get(2, String.class);
                int tookCount = tuple.get(3, Integer.class);

                LocalDateTime currentLocalDateTime = LocalDate.parse(yesterDay, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay();
                LocalDateTime endLocalDateTime = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE).atStartOfDay();
                // 미션 몇번째 날인지 계산
                int missionOrder = missionType - Long.valueOf(Duration.between(endLocalDateTime, currentLocalDateTime).toDays()).intValue();
                // 미션 성공 여부 체크
                int successFlag = (missionOrder * 2) == tookCount ? 1 : 0;

                // 1. 미션 성공으로 완료 시
                // 2. 미션 실패 시
                if ((missionType == missionOrder && successFlag == 1)
                        || successFlag == 0) {

                    User user = userService.selectUserInfo(missionId);
                    int tier = user.getTier();
                    int tierPoint = user.getTierPoint();

                    // 처음 시작한 유저일 경우 티어를 브론즈4로 맞춘다
                    if (tier == -1 && tierPoint == 0) {
                        tier = 0;
                    }

                    int beforeTierRealPoint = tier * 100 + tierPoint;
                    int afterTierRealPoint;
                    MissionInfo missionInfo = MissionInfo.find(missionType);
                    int rewardPoint = missionInfo.getRewardPoint();

                    // 미션 성공
                    if (successFlag == 1) {
                        afterTierRealPoint = beforeTierRealPoint + rewardPoint;
                    } else {
                        TierInfo beforeTierInfo = TierInfo.findByTier(tier);
                        rewardPoint = (int) Math.ceil(rewardPoint * beforeTierInfo.getPenalty() * -1);
                        afterTierRealPoint = beforeTierRealPoint + rewardPoint;
                    }

                    // 티어 포인트의 최대 최소값을 조정
                    if (afterTierRealPoint < 0) {
                        afterTierRealPoint = 0;
                    }

                    if (afterTierRealPoint > 2299) {
                        afterTierRealPoint = 2299;
                    }

                    // mission proceeding, successFlag 변경
                    missionService.updateProceedingAndSuccessFlag(missionId, successFlag);

                    // tier, tierPoint 변경
                    TierInfo afterTierInfo = TierInfo.findByTierPoint(afterTierRealPoint);
                    user.updateTierInfo(afterTierInfo.getTier(), afterTierRealPoint - (afterTierRealPoint / 100 * 100));

                    // mission_history 저장
                    missionService.saveMissionHistory(MissionHistory.builder()
                            .missionId(missionId)
                            .missionType(missionType)
                            .date(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE))
                            .successFlag(successFlag)
                            .changeTierPoint(rewardPoint)
                            .tookCount(tookCount)
                            .build());

                }
            }
        });
    }

    @SuppressWarnings("all")
    public void monthSchedule() {
        LocalDate localDate = LocalDate.now();
        int localDateYear = localDate.getYear();
        String thisMonth = String.format("%02d", localDate.getMonthValue());
        String thisMonthCondition = localDateYear + thisMonth + "01";

        LocalDate lastLocalDate = localDate.minusMonths(1);
        int lastLocalDateYear = lastLocalDate.getYear();
        String lastMonth = String.format("%02d", lastLocalDate.getMonthValue());
        String lastMonthCondition = lastLocalDateYear + lastMonth + "01";

        final int HUICK_HALF = 15;
        final int HUICK_FULL = YearMonth.from(LocalDate.now().minusMonths(1)).lengthOfMonth();

        List<Tuple> tupleList =  missionService.selectLastMission(lastMonthCondition, thisMonthCondition);

        tupleList.forEach(tuple -> {
            if (tuple != null) {
                UserId userId = tuple.get(0, UserId.class);
                int successCount = tuple.get(1, Integer.class);

                System.out.println(userId);
                System.out.println(successCount);
            }
        });
    }
}
