package com.fourtwod.service.user;

import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.mission.Mission;
import com.fourtwod.domain.mission.MissionDetailRepository;
import com.fourtwod.domain.mission.MissionRepository;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.domain.user.UserRepository;
import com.fourtwod.service.TierInfo;
import com.fourtwod.web.dto.RankingDto;
import com.fourtwod.web.dto.UserDto;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.fourtwod.domain.mission.QMission.mission;
import static com.fourtwod.domain.user.QUser.user;

@RequiredArgsConstructor
@Service
public class UserService {

    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final MissionDetailRepository missionDetailRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public ResponseCode checkOrUpdateNickName(int flag, String nickname, SessionUser user) {
        // flag 체크
        switch (flag) {
            case 0: case 1: break;
            default: return ResponseCode.COMM_E001;
        }

        String registrationId = user.getRegistrationId();
        User userByEmail = userRepository.findByUserId(UserId.builder()
                .email(user.getEmail())
                .registrationId(registrationId)
                .build())
                .orElse(null);

        // Email이 검색되지 않을 경우
        if (userByEmail == null) {
            return ResponseCode.COMM_E001;
        }

        // 2자 이상 10자 이하 닉네임이 아닌 경우
        if (nickname.length() < 2 || nickname.length() > 10) {
            return ResponseCode.NICK_E000;
        }

        // 한글,영문,숫자 닉네임이 아닌 경우
        if (!Pattern.matches("^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+$", nickname)) {
            return ResponseCode.NICK_E001;
        }

        User userByNickname = userRepository.findByNickname(nickname).orElse(null);

        // 닉네임이 중복될 경우
        if (userByNickname != null) {
            return ResponseCode.NICK_E002;
        }

        // 사용할 수 없는 로그인 수단인 경우
        if (!"google".equals(registrationId) && !"naver".equals(registrationId) && !"kakao".equals(registrationId)) {
            return ResponseCode.COMM_E001;
        }

        // 1인 경우에만 닉네임 변경
        if (flag == 1) {
            SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
            sessionUser.setNickname(nickname);

            httpSession.setAttribute("user", sessionUser);
            userByEmail.updateNickname(nickname);
        }

        return ResponseCode.COMM_S000;
    }

    @Transactional
    public void reset() {
        missionDetailRepository.deleteAll();
        missionRepository.deleteAll();
        userRepository.deleteAll();
    }

    public ApiResult selectRankList(int rankType, SessionUser sessionUser) {
        // 랭크 타입 체크
        switch (rankType) {
            case 0: case 1: break;
            default: return new ApiResult<>(ResponseCode.COMM_E002);
        }

        // 응답 dto
        List<RankingDto> rankDtoList = new ArrayList<>();
        List<User> userList = new ArrayList<>();

        // 랭크 조회
        // 전체 랭크
        if (rankType == 0) {
            userList = jpaQueryFactory.selectFrom(user)
                    .orderBy(user.tier.desc(), user.tierPoint.desc(), user.nickname.asc())
                    .fetch();
        // 진행중인 미션 랭크
        } else {
            Mission _mission = jpaQueryFactory.selectFrom(mission)
                    .join(user)
                        .on(mission.user.userId.eq(user.userId))
                    .where(user.userId.email.eq(sessionUser.getEmail())
                            .and(user.userId.registrationId.eq(sessionUser.getRegistrationId()))
                            .and(mission.proceeding.eq(1)))
                    .fetchOne();

            if (_mission != null) {
                userList = jpaQueryFactory.selectFrom(user)
                        .join(mission)
                            .on(user.userId.eq(mission.user.userId))
                        .where(mission.missionType.eq(_mission.getMissionType())
                                .and(mission.date.eq(_mission.getDate())))
                        .orderBy(user.tier.desc(), user.tierPoint.desc(), user.nickname.asc())
                        .fetch();
            }
        }

        for (int i = 1; i <= userList.size(); i++) {
            User _user = userList.get(i - 1);

            // 20등까지만 추가
            if (i <= 20) {
                rankDtoList.add(RankingDto.builder()
                        .ranking(i)
                        .tier(_user.getTier())
                        .tierPoint(_user.getTierPoint())
                        .nickname(_user.getNickname())
                        .build());
            } else {
                if (_user.getUserId().getEmail().equals(sessionUser.getEmail())
                        && _user.getUserId().getRegistrationId().equals(sessionUser.getRegistrationId())) {

                    rankDtoList.add(RankingDto.builder()
                            .ranking(i)
                            .tier(_user.getTier())
                            .tierPoint(_user.getTierPoint())
                            .nickname(_user.getNickname())
                            .build());
                }
            }
        }

        return new ApiResult<>(rankDtoList);
    }

    public ApiResult selectUserInfo(SessionUser sessionUser) {
        User user = userRepository.findByUserId(UserId.builder()
                        .email(sessionUser.getEmail())
                        .registrationId(sessionUser.getRegistrationId())
                        .build())
                .orElse(null);

        TierInfo tierInfo = TierInfo.findByTier(user.getTier());
        TierInfo nextTierInfo = TierInfo.findByTier(tierInfo.getTier() + 1);

        // 다음 티어는 없으므로 최대 티어로 조정
        if (nextTierInfo == TierInfo.unranked) {
            nextTierInfo = TierInfo.master1;
        }

        String nextTier = nextTierInfo.name();
        String nextTierName = nextTier.substring(0, nextTier.length() - 1).toUpperCase();
        String nextTierValue = nextTier.substring(nextTier.length() - 1);

        if (user != null) {
            return new ApiResult<>(UserDto.builder()
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .tier(tierInfo.name())
                    .nextTier(nextTierName + " " + nextTierValue)
                    .tierPoint(user.getTierPoint())
                    .build());
        } else {
            return new ApiResult<>(ResponseCode.COMM_E001);
        }
    }

    public User selectUserInfo(long missionId) {
        return jpaQueryFactory.selectFrom(user)
                .join(mission).on(user.userId.eq(mission.user.userId))
                .where(mission.missionId.eq(missionId)).fetchOne();
    }
}
