package com.fourtwod.service.mission;

import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.mission.*;
import com.fourtwod.domain.user.QUser;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.web.dto.MissionDto;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionDetailRepository missionDetailRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public MissionDto selectMissionInProgress(SessionUser sessionUser) {
        QUser user = QUser.user;
        QMission mission = QMission.mission;
        QMissionDetail missionDetail = QMissionDetail.missionDetail;

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
            default: return new ApiResult<>(ResponseCode.MISS_E000);
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
            return new ApiResult<>(ResponseCode.MISS_E001);
        }

        // 미션 생성
        Mission mission = missionRepository.save(Mission.builder()
                .missionType(missionType)
                .proceeding(1)
                .user(user)
                .build());

        List<MissionDto.Detail> detailList = new ArrayList<>();
        // 응답 dto
        MissionDto missionDto = MissionDto.builder()
                .missionType(missionType)
                .detail(detailList)
                .build();

        LocalDate localDate = LocalDate.now();

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
}
