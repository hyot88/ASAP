package com.fourtwod.service.mission;

import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.mission.*;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.web.dto.MissionDto;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.fourtwod.domain.mission.QMission.mission;
import static com.fourtwod.domain.mission.QMissionDetail.missionDetail;
import static com.fourtwod.domain.user.QUser.user;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionDetailRepository missionDetailRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public MissionDto selectMissionInProgress(SessionUser sessionUser) {
        // 미션 조회
        List<MissionDetail> list = jpaQueryFactory.selectFrom(missionDetail)
                .join(mission)
                    .on(missionDetail.missionDetailId.missionDetailId.eq(mission.missionId))
                .join(user)
                    .on(mission.user.userId.eq(user.userId))
                .where(user.userId.email.eq(sessionUser.getEmail())
                        .and(user.userId.registrationId.eq(sessionUser.getRegistrationId()))
                        .and(mission.proceeding.eq(1)))
                .fetch();

        // 응답 dto
        MissionDto missionDto = MissionDto.builder()
                .missionType(-1)
                .detail(new ArrayList<>())
                .build();

        list.forEach(tmpDetail -> {
            if (tmpDetail != null) {
                if (missionDto.getMissionType() == -1) {
                    missionDto.setMissionType(tmpDetail.getMission().getMissionType());
                }

                missionDto.getDetail().add(MissionDto.Detail.builder()
                        .date(tmpDetail.getMissionDetailId().getDate())
                        .afternoon(tmpDetail.getAfternoon())
                        .night(tmpDetail.getNight())
                        .build());
            }
        });

        return missionDto;
    }

    @Transactional
    public ApiResult createMission(int missionType, SessionUser sessionUser) {
        // 미션 타입 체크
        switch (missionType) {
            case 1: case 3: case 5: case 7: break;
            default: return new ApiResult<>(ResponseCode.COMM_E002);
        }

        User user = User.builder()
                .userId(UserId.builder()
                        .email(sessionUser.getEmail())
                        .registrationId(sessionUser.getRegistrationId())
                        .build())
                .build();

        // 진행중 미션 여부 체크
        Mission missionInProgress = missionRepository.findByUserAndProceeding(user, 1).orElse(null);

        if (missionInProgress != null) {
            return new ApiResult<>(ResponseCode.MISS_E000);
        }

        LocalDate localDate = LocalDate.now();

        // 미션 생성
        Mission mission = missionRepository.save(Mission.builder()
                .missionType(missionType)
                .date(localDate.format(DateTimeFormatter.BASIC_ISO_DATE))
                .proceeding(1)
                .user(user)
                .build());

        List<MissionDto.Detail> detailList = new ArrayList<>();
        // 응답 dto
        MissionDto missionDto = MissionDto.builder()
                .missionType(missionType)
                .detail(detailList)
                .build();

        for (int i = 0; i < missionType; i++) {
            // 미션 디테일 생성
            String strLocalDate = localDate.plusDays(i).format(DateTimeFormatter.BASIC_ISO_DATE);
            MissionDetail missionDetail = missionDetailRepository.save(MissionDetail.builder()
                    .missionDetailId(MissionDetailId.builder()
                            .missionDetailId(mission.getMissionId())
                            .date(strLocalDate)
                            .build())
                    .afternoon(0)
                    .night(0)
                    .mission(mission)
                    .build());

            detailList.add(MissionDto.Detail.builder()
                    .date(missionDetail.getMissionDetailId().getDate())
                    .afternoon(missionDetail.getAfternoon())
                    .night(missionDetail.getNight())
                    .build());
        }

        return new ApiResult<>(missionDto);
    }

    @Transactional
    public ResponseCode tookEvent(String date, int time, SessionUser sessionUser) {
        // date 체크
        if (date.length() != 8) {
            return ResponseCode.COMM_E002;
        }

        // time 체크
        switch (time) {
            case 0: case 1: break;
            default: return ResponseCode.COMM_E002;
        }

        // 참여 가능한 시간 체크
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime startLimit;
        LocalDateTime endLimit;

        //TODO: 테스트를 위해 참여 가능 시간을 임시로 변경함
        /*if (time == 0) {
            startLimit = localDateTime.withHour(6).withMinute(0).withSecond(0).withNano(0);
            endLimit = localDateTime.withHour(12).withSecond(0).withSecond(30).withNano(0);
        } else {
            startLimit = localDateTime.withHour(18).withMinute(0).withSecond(0).withNano(0);
            endLimit = localDateTime.plusDays(1).withHour(0).withSecond(0).withSecond(30).withNano(0);
        }*/
        startLimit = localDateTime.minusDays(1);
        endLimit = localDateTime.plusDays(1);

        if (!(localDateTime.isAfter(startLimit) && localDateTime.isBefore(endLimit))) {
            return ResponseCode.MISS_E001;
        }

        // 미션 참여 여부 업데이트
        JPAUpdateClause jpaUpdateClause = jpaQueryFactory.update(missionDetail);

        if (time == 0) {
            jpaUpdateClause.set(missionDetail.afternoon, 1);
        } else {
            jpaUpdateClause.set(missionDetail.night, 1);
        }

        long lResult = jpaUpdateClause.where(missionDetail.eq(
                JPAExpressions.selectFrom(missionDetail)
                        .join(mission)
                            .on(missionDetail.missionDetailId.missionDetailId.eq(mission.missionId))
                        .join(user)
                            .on(mission.user.userId.eq(user.userId))
                        .where(user.userId.email.eq(sessionUser.getEmail())
                            .and(user.userId.registrationId.eq(sessionUser.getRegistrationId()))
                            .and(mission.proceeding.eq(1))
                            .and(missionDetail.missionDetailId.date.eq(date))))).execute();

        if (lResult == 0) {
            return ResponseCode.MISS_E002;
        }

        return ResponseCode.COMM_S000;
    }

    @Transactional
    public List<Tuple> selectEndedMission() {
        //TODO: 테스트를 위해서 임시로 yesterDay를 조정
//        String yesterDay = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
        String yesterDay = LocalDate.now().plusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE);

        return jpaQueryFactory.select(missionDetail.missionDetailId.missionDetailId
                    , missionDetail.afternoon.min().add(missionDetail.night.min()))
                .from(missionDetail)
                .join(mission)
                    .on(missionDetail.missionDetailId.missionDetailId.eq(mission.missionId))
                .join(user)
                    .on(mission.user.userId.eq(user.userId))
                .groupBy(missionDetail.missionDetailId.missionDetailId)
                .having(missionDetail.missionDetailId.date.max().eq(yesterDay))
                .where(mission.proceeding.eq(1)).fetch();
    }

    public boolean checkForCompleMission(Mission mission) {
        return false;
    }
}
